/**
 * Authentication & Session Manager (Demo System)
 * Manages current logged in user role, credentials, UI updates, and strict logout handling
 */

const AuthManager = {
    STORAGE_KEY: 'feedback_flow_user',

    // Pre-configured demo user accounts
    DEMO_USERS: {
        student: { id: 1, name: 'Alex Johnson', role: 'student', username: 'student', studentIdentifier: 'STU1001' },
        faculty: { id: 1, name: 'Dr. John Doe', role: 'faculty', username: 'faculty', department: 'Computer Science' },
        admin: { id: 1, name: 'System Administrator', role: 'admin', username: 'admin' }
    },

    getCurrentUser() {
        try {
            const saved = localStorage.getItem(this.STORAGE_KEY);
            return saved ? JSON.parse(saved) : null;
        } catch (e) {
            return null;
        }
    },

    login(username, password, roleHint = 'student') {
        let user = null;
        const normalizedUser = (username || '').toLowerCase().trim();

        if (normalizedUser === 'admin' || roleHint === 'admin') {
            user = this.DEMO_USERS.admin;
        } else if (normalizedUser === 'faculty' || roleHint === 'faculty') {
            user = this.DEMO_USERS.faculty;
        } else {
            user = {
                ...this.DEMO_USERS.student,
                username: username,
                name: username ? username.charAt(0).toUpperCase() + username.slice(1) : 'Student User'
            };
        }

        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
        this.updateUI(user);
        
        if (window.API?.showToast) {
            window.API.showToast(`Welcome back, ${user.name}!`, 'success');
        }
        return user;
    },

    // Standardized Single Logout Function
    logout() {
        // 1. Clear session and all role indicators from localStorage
        localStorage.removeItem(this.STORAGE_KEY);
        localStorage.removeItem('feedback_flow_role');
        localStorage.removeItem('feedback_flow_session');

        // 2. Close sidebar if open
        if (window.Navigation) {
            window.Navigation.closeSidebar();
        }

        // 3. Close any open modals
        document.querySelectorAll('.modal-overlay').forEach(modal => {
            modal.classList.add('hidden');
            modal.classList.remove('open', 'active');
        });
        document.body.style.overflow = '';

        // 4. Update UI to logged-out state
        this.updateUI(null);

        // 5. Redirect/Switch to Landing View
        if (window.Navigation) {
            window.Navigation.switchView('landing-view');
        }

        if (window.API?.showToast) {
            window.API.showToast('Logged out successfully', 'info');
        }
    },

    updateUI(user) {
        const authHeaderAction = document.getElementById('auth-header-action');
        const sidebarUserInfo = document.getElementById('sidebar-user-info');
        const sidebarUserName = document.getElementById('sidebar-user-name');
        const sidebarUserRole = document.getElementById('sidebar-user-role');
        const sidebarUserAvatar = document.getElementById('sidebar-user-avatar');
        const sidebarLoginBtn = document.getElementById('sidebar-login-btn');
        const sidebarLogoutBtn = document.getElementById('sidebar-logout-btn');

        const roleNavs = document.querySelectorAll('.role-nav');

        if (user) {
            // Update Header Action Area with User Pill & Dedicated Logout Button
            if (authHeaderAction) {
                authHeaderAction.innerHTML = `
                    <div class="user-header-pill">
                        <span class="avatar-sm">${user.name.charAt(0).toUpperCase()}</span>
                        <span class="user-role-label">${user.role.toUpperCase()}</span>
                        <button id="header-logout-btn" class="btn btn-logout btn-sm" title="Log out">
                            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                <polyline points="16 17 21 12 16 7"></polyline>
                                <line x1="21" y1="12" x2="9" y2="12"></line>
                            </svg>
                            <span>Logout</span>
                        </button>
                    </div>
                `;
                document.getElementById('header-logout-btn')?.addEventListener('click', () => this.logout());
            }

            // Update Sidebar User Card
            if (sidebarUserInfo) sidebarUserInfo.classList.remove('hidden');
            if (sidebarUserName) sidebarUserName.textContent = user.name;
            if (sidebarUserRole) sidebarUserRole.textContent = user.role.toUpperCase();
            if (sidebarUserAvatar) sidebarUserAvatar.textContent = user.name.charAt(0).toUpperCase();
            if (sidebarLoginBtn) sidebarLoginBtn.classList.add('hidden');
            if (sidebarLogoutBtn) sidebarLogoutBtn.classList.remove('hidden');

            // Show role-specific navigation links
            roleNavs.forEach(nav => {
                if (nav.dataset.role === user.role) {
                    nav.classList.remove('hidden');
                } else {
                    nav.classList.add('hidden');
                }
            });
        } else {
            // Logged Out Header Action
            if (authHeaderAction) {
                authHeaderAction.innerHTML = `
                    <button id="open-login-modal-btn" class="btn btn-primary">Sign In</button>
                `;
                document.getElementById('open-login-modal-btn')?.addEventListener('click', () => {
                    window.Navigation?.openModal('login-modal');
                });
            }

            if (sidebarUserInfo) sidebarUserInfo.classList.add('hidden');
            if (sidebarLoginBtn) sidebarLoginBtn.classList.remove('hidden');
            if (sidebarLogoutBtn) sidebarLogoutBtn.classList.add('hidden');

            roleNavs.forEach(nav => nav.classList.add('hidden'));
        }
    },

    init() {
        const user = this.getCurrentUser();
        this.updateUI(user);

        document.getElementById('sidebar-logout-btn')?.addEventListener('click', () => this.logout());
        document.getElementById('sidebar-login-btn')?.addEventListener('click', () => {
            if (window.Navigation) {
                window.Navigation.closeSidebar();
                window.Navigation.openModal('login-modal');
            }
        });
    }
};

document.addEventListener('DOMContentLoaded', () => AuthManager.init());
window.AuthManager = AuthManager;
