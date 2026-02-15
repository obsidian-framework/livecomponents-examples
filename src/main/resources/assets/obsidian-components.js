class ObsidianComponents {
    constructor() {
        this.components = new Map();
        this.csrfToken = this.getCsrfToken();
        this.init();
    }

    init() {
        this.discoverComponents();
        this.attachEventListeners();
        console.log('Obsidian LiveComponents initialized:', this.components.size, 'components found');
    }

    discoverComponents() {
        document.querySelectorAll('[live\\:id]').forEach(el => {
            const componentId = el.getAttribute('live:id');
            if (!this.components.has(componentId)) {
                this.components.set(componentId, {
                    element: el,
                    loading: false,
                    pollInterval: null
                });
                this.attachModelBindings(el, componentId);
                this.attachPolling(el, componentId);
                this.attachInit(el, componentId);
            }
        });
    }

    attachEventListeners() {
        document.addEventListener('click', (e) => {
            const target = e.target.closest('[live\\:click]');
            if (!target) return;

            e.preventDefault();

            // Check for confirmation
            const confirmMessage = target.getAttribute('live:confirm');
            if (confirmMessage) {
                if (!confirm(confirmMessage)) {
                    return; // User cancelled
                }
            }

            const action = target.getAttribute('live:click');
            const component = target.closest('[live\\:id]');

            if (component) {
                const componentId = component.getAttribute('live:id');
                this.call(componentId, action);
            }
        });
    }

    attachModelBindings(element, componentId) {
        // Find all inputs with live:model
        const modelInputs = element.querySelectorAll('[live\\:model]');

        modelInputs.forEach(input => {
            const fieldName = input.getAttribute('live:model');
            const debounceTime = parseInt(input.getAttribute('live:debounce')) || 300;
            const updateOnBlur = input.hasAttribute('live:blur');
            const updateOnEnter = input.hasAttribute('live:lazy');

            if (updateOnBlur) {
                // Update only on blur
                input.addEventListener('blur', () => {
                    this.updateModel(componentId, fieldName, input.value);
                });
            } else if (updateOnEnter) {
                // Update only on Enter key
                input.addEventListener('keydown', (e) => {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        this.updateModel(componentId, fieldName, input.value);
                    }
                });
            } else {
                // Update on input with debounce (default)
                const debouncedUpdate = this.debounce((value) => {
                    this.updateModel(componentId, fieldName, value);
                }, debounceTime);

                input.addEventListener('input', (e) => {
                    debouncedUpdate(e.target.value);
                });
            }
        });
    }

    updateModel(componentId, fieldName, value) {
        this.call(componentId, '', { field: fieldName, value: value });
    }

    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    attachPolling(element, componentId) {
        const pollAttr = element.getAttribute('live:poll');
        if (!pollAttr) return;

        // Parse interval: "5000" or "5s" or "5m"
        let interval = parseInt(pollAttr);
        if (pollAttr.endsWith('s')) {
            interval = parseInt(pollAttr) * 1000;
        } else if (pollAttr.endsWith('m')) {
            interval = parseInt(pollAttr) * 60000;
        }

        // Get optional action name (live:poll.5s="refreshStats")
        let action = null;
        for (let attr of element.attributes) {
            if (attr.name.startsWith('live:poll.') && !attr.name.includes('live:poll.class')) {
                action = attr.value;
                break;
            }
        }

        const component = this.components.get(componentId);
        if (component) {
            // Clear existing interval if any
            if (component.pollInterval) {
                clearInterval(component.pollInterval);
            }

            // Set new polling interval
            component.pollInterval = setInterval(() => {
                if (!document.contains(element)) {
                    // Component removed from DOM, stop polling
                    clearInterval(component.pollInterval);
                    return;
                }

                // Call action or just refresh
                if (action) {
                    this.call(componentId, action);
                } else {
                    this.call(componentId, '__refresh');
                }
            }, interval);
        }
    }

    attachInit(element, componentId) {
        const initAction = element.getAttribute('live:init');
        if (initAction) {
            // Call init action after a short delay to ensure component is mounted
            setTimeout(() => {
                this.call(componentId, initAction);
            }, 100);
        }
    }

    async call(componentId, action, customParams = {}) {
        const component = this.components.get(componentId);
        if (!component || component.loading) return;

        try {
            component.loading = true;
            this.showLoading(component.element);

            const state = this.captureState(component.element);

            // Parse action name and parameters from string like "vote('Functional')"
            const parsed = this.parseAction(action);

            // Merge parsed params with custom params (for __updateModel)
            const finalParams = customParams.field ?
                [customParams.value] : // For __updateModel, pass value directly
                parsed.params;

            const response = await fetch('/obsidian/components', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': this.csrfToken
                },
                body: JSON.stringify({
                    componentId: componentId,
                    action: customParams.field ? `updateField_${customParams.field}` : parsed.name,
                    state: state,
                    params: finalParams
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            if (data.success) {
                this.updateComponent(componentId, data.html);
            } else {
                console.error('Component error:', data.error);
                this.showError(component.element, data.error);
            }
        } catch (error) {
            console.error('LiveComponent error:', error);
            this.showError(component.element, error.message);
        } finally {
            component.loading = false;
            this.hideLoading(component.element);
        }
    }

    captureState(element) {
        const state = {};

        // Capture all input, textarea, select values
        const inputs = element.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            const id = input.id;
            if (id) {
                // Use ID as key
                const key = id.replace(/^.*-/, ''); // Remove component ID prefix
                if (input.type === 'checkbox') {
                    state[key] = input.checked;
                } else if (input.type === 'radio') {
                    if (input.checked) {
                        state[input.name] = input.value;
                    }
                } else {
                    state[key] = input.value;
                }
            }
        });

        return state;
    }

    updateComponent(componentId, html) {
        const component = this.components.get(componentId);
        if (!component) return;

        // Check if element is still in the DOM
        if (!document.contains(component.element)) {
            console.warn('Component element no longer in DOM:', componentId);
            this.components.delete(componentId);
            return;
        }

        // Check if element has a parent
        if (!component.element.parentNode) {
            console.warn('Component element has no parent:', componentId);
            this.components.delete(componentId);
            return;
        }

        // Replace the element
        try {
            const parent = component.element.parentNode;
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            const newElement = tempDiv.firstElementChild;

            parent.replaceChild(newElement, component.element);

            // Update reference and re-discover
            this.components.set(componentId, {
                element: newElement,
                loading: false,
                pollInterval: component.pollInterval
            });

            // Re-attach event listeners for the new element
            this.attachModelBindings(newElement, componentId);
        } catch (error) {
            console.error('Failed to update component:', componentId, error);
        }
    }

    showLoading(element) {
        // Show loading indicators (display style)
        const loadingIndicators = element.querySelectorAll('[live\\:loading]');
        loadingIndicators.forEach(indicator => {
            // Check if it has .class modifier
            const classList = indicator.getAttribute('live:loading.class');
            const addClasses = indicator.getAttribute('live:loading.add');
            const removeClasses = indicator.getAttribute('live:loading.remove');

            if (classList) {
                // Add specific classes
                classList.split(' ').forEach(cls => indicator.classList.add(cls));
            } else if (addClasses || removeClasses) {
                if (addClasses) {
                    addClasses.split(' ').forEach(cls => indicator.classList.add(cls));
                }
                if (removeClasses) {
                    removeClasses.split(' ').forEach(cls => indicator.classList.remove(cls));
                }
            } else {
                // Default: show by changing display
                indicator.style.display = '';
            }
        });

        // Disable buttons
        const buttons = element.querySelectorAll('button[live\\:click], [live\\:click]');
        buttons.forEach(btn => {
            btn.disabled = true;
            btn.style.opacity = '0.6';
            btn.style.cursor = 'not-allowed';
        });
    }

    hideLoading(element) {
        // Hide loading indicators
        const loadingIndicators = element.querySelectorAll('[live\\:loading]');
        loadingIndicators.forEach(indicator => {
            const classList = indicator.getAttribute('live:loading.class');
            const addClasses = indicator.getAttribute('live:loading.add');
            const removeClasses = indicator.getAttribute('live:loading.remove');

            if (classList) {
                // Remove specific classes
                classList.split(' ').forEach(cls => indicator.classList.remove(cls));
            } else if (addClasses || removeClasses) {
                if (addClasses) {
                    addClasses.split(' ').forEach(cls => indicator.classList.remove(cls));
                }
                if (removeClasses) {
                    removeClasses.split(' ').forEach(cls => indicator.classList.add(cls));
                }
            } else {
                // Default: hide by changing display
                indicator.style.display = 'none';
            }
        });

        // Re-enable buttons
        const buttons = element.querySelectorAll('button[live\\:click], [live\\:click]');
        buttons.forEach(btn => {
            btn.disabled = false;
            btn.style.opacity = '';
            btn.style.cursor = '';
        });
    }

    showError(element, message) {
        const errorDiv = document.createElement('div');
        errorDiv.style.cssText = 'background: #ef4444; color: white; padding: 1rem; margin: 1rem 0; border-radius: 0.5rem;';
        errorDiv.textContent = 'Error: ' + message;
        element.insertBefore(errorDiv, element.firstChild);
        setTimeout(() => errorDiv.remove(), 5000);
    }

    getCsrfToken() {
        const meta = document.querySelector('meta[name="csrf-token"]');
        if (meta && meta.content) return meta.content;

        const cookies = document.cookie.split(';');
        for (let cookie of cookies) {
            const [name, value] = cookie.trim().split('=');
            if (name === 'CSRF-TOKEN' || name === '_csrf') {
                return decodeURIComponent(value);
            }
        }
        return null;
    }

    parseAction(actionString) {
        // Parse "vote('Functional')" or "deleteTodo(42)" or "update('name', 'John')"
        const match = actionString.match(/^(\w+)\((.*)\)$/);

        if (!match) {
            // Simple action without params: "increment"
            return { name: actionString, params: [] };
        }

        const name = match[1];
        const paramsString = match[2];

        if (!paramsString.trim()) {
            // Empty params: "reset()"
            return { name, params: [] };
        }

        // Parse parameters
        const params = [];
        let current = '';
        let inString = false;
        let stringChar = null;

        for (let i = 0; i < paramsString.length; i++) {
            const char = paramsString[i];

            if ((char === '"' || char === "'") && paramsString[i-1] !== '\\') {
                if (!inString) {
                    inString = true;
                    stringChar = char;
                } else if (char === stringChar) {
                    inString = false;
                    stringChar = null;
                }
                continue;
            }

            if (char === ',' && !inString) {
                params.push(this.parseValue(current.trim()));
                current = '';
            } else {
                current += char;
            }
        }

        if (current.trim()) {
            params.push(this.parseValue(current.trim()));
        }

        return { name, params };
    }

    parseValue(value) {
        // Parse different types: 'string', 42, true, false, null
        if (value === 'true') return true;
        if (value === 'false') return false;
        if (value === 'null') return null;
        if (!isNaN(value) && value !== '') return Number(value);
        return value; // String
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        window.ObsidianComponents = new ObsidianComponents();
    });
} else {
    window.ObsidianComponents = new ObsidianComponents();
}