/**
 * Login Modal Controller
 * Manages role pills selection, auto-filling credentials, and login submission
 */

const LoginController = {
    selectedRole: 'student',

    init() {
        this.bindRolePills();
        this.bindLoginForm();
    },

    bindRolePills() {
        const pills = document.querySelectorAll('.role-pill');
        const usernameInput = document.getElementById('login-username');
        const passwordInput = document.getElementById('login-password');

        pills.forEach(pill => {
            pill.addEventListener('click', () => {
                // Update active state
                pills.forEach(p => p.classList.remove('active'));
                pill.classList.add('active');

                // Read dataset values
                const role = pill.dataset.role;
                const user = pill.dataset.user;
                const pass = pill.dataset.pass;

                this.selectedRole = role;

                if (usernameInput) usernameInput.value = user;
                if (passwordInput) passwordInput.value = pass;
            });
        });
    },

    bindLoginForm() {
        const form = document.getElementById('login-form');
        if (!form) return;

        form.addEventListener('submit', (e) => {
            e.preventDefault();
            const username = document.getElementById('login-username').value.trim();
            const password = document.getElementById('login-password').value.trim();

            if (!username || !password) {
                API.showToast('Please enter both username and password', 'error');
                return;
            }

            // Perform demo authentication via AuthManager
            const user = AuthManager.login(username, password, this.selectedRole);

            // Close login modal
            if (window.Navigation) {
                window.Navigation.closeModal('login-modal');
                // Switch to corresponding role portal view
                window.Navigation.switchView(`${user.role}-view`);
            }
        });
    }
};

document.addEventListener('DOMContentLoaded', () => LoginController.init());
window.LoginController = LoginController;
