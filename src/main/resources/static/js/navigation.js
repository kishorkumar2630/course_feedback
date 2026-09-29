/**
 * Navigation & Modal Controller
 * Handles slide-out sidebar, route view switching, view authentication protection, and modals
 */

const Navigation = {
    init() {
        this.bindSidebarEvents();
        this.bindViewRouting();
        this.bindModalEvents();
        this.bindHeroButtons();
    },

    // Sidebar Slide-Out Handler
    bindSidebarEvents() {
        const toggleBtn = document.getElementById('sidebar-toggle-btn');
        const closeBtn = document.getElementById('sidebar-close-btn');
        const backdrop = document.getElementById('sidebar-backdrop');

        if (toggleBtn) {
            toggleBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.toggleSidebar();
            });
        }

        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.closeSidebar());
        }

        if (backdrop) {
            backdrop.addEventListener('click', () => this.closeSidebar());
        }
    },

    toggleSidebar() {
        const sidebar = document.getElementById('app-sidebar');
        const backdrop = document.getElementById('sidebar-backdrop');
        if (sidebar && backdrop) {
            sidebar.classList.toggle('open');
            sidebar.classList.toggle('active');
            backdrop.classList.toggle('open');
            backdrop.classList.toggle('active');
        }
    },

    openSidebar() {
        const sidebar = document.getElementById('app-sidebar');
        const backdrop = document.getElementById('sidebar-backdrop');
        if (sidebar && backdrop) {
            sidebar.classList.add('open', 'active');
            backdrop.classList.add('open', 'active');
        }
    },

    closeSidebar() {
        const sidebar = document.getElementById('app-sidebar');
        const backdrop = document.getElementById('sidebar-backdrop');
        if (sidebar && backdrop) {
            sidebar.classList.remove('open', 'active');
            backdrop.classList.remove('open', 'active');
        }
    },

    // View Section Routing with Auth Protection
    bindViewRouting() {
        const navItems = document.querySelectorAll('.sidebar-nav .nav-item');
        navItems.forEach(item => {
            item.addEventListener('click', (e) => {
                const targetViewId = item.dataset.target;
                if (targetViewId) {
                    this.switchView(targetViewId);
                    this.closeSidebar();

                    // Update active nav state
                    navItems.forEach(n => n.classList.remove('active'));
                    item.classList.add('active');
                }
            });
        });
    },

    switchView(viewId) {
        const user = window.AuthManager?.getCurrentUser();

        // Security Guard: Check session for protected role portals
        if (viewId === 'student-view' && (!user || user.role !== 'student')) {
            viewId = 'landing-view';
            if (!user) this.openModal('login-modal');
        } else if (viewId === 'faculty-view' && (!user || user.role !== 'faculty')) {
            viewId = 'landing-view';
            if (!user) this.openModal('login-modal');
        } else if (viewId === 'admin-view' && (!user || user.role !== 'admin')) {
            viewId = 'landing-view';
            if (!user) this.openModal('login-modal');
        }

        const views = document.querySelectorAll('.view-section');
        views.forEach(view => {
            if (view.id === viewId) {
                view.classList.remove('hidden');
                view.classList.add('active');
            } else {
                view.classList.add('hidden');
                view.classList.remove('active');
            }
        });

        // Trigger portal load functions
        if (viewId === 'student-view' && window.StudentPortal) {
            window.StudentPortal.loadData();
        } else if (viewId === 'faculty-view' && window.FacultyPortal) {
            window.FacultyPortal.loadData();
        } else if (viewId === 'admin-view' && window.AdminPortal) {
            window.AdminPortal.loadData();
        } else if (viewId === 'analytics-view' && window.AnalyticsManager) {
            window.AnalyticsManager.populateFormsSelect();
        }
    },

    // Modal Control
    bindModalEvents() {
        const closeBtns = document.querySelectorAll('[id^="close-"], [id^="cancel-"]');
        closeBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const modal = btn.closest('.modal-overlay');
                if (modal) this.closeModal(modal.id);
            });
        });

        const modals = document.querySelectorAll('.modal-overlay');
        modals.forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.closeModal(modal.id);
                }
            });
        });
    },

    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('hidden');
            modal.classList.add('open', 'active');
            document.body.style.overflow = 'hidden';
        }
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('hidden');
            modal.classList.remove('open', 'active');
            document.body.style.overflow = '';
        }
    },

    // Landing Page Hero Buttons
    bindHeroButtons() {
        const getStartedBtn = document.getElementById('hero-get-started-btn');
        const demoLoginBtn = document.getElementById('hero-demo-login-btn');

        if (getStartedBtn) {
            getStartedBtn.addEventListener('click', () => {
                const user = window.AuthManager?.getCurrentUser();
                if (user) {
                    this.switchView(`${user.role}-view`);
                } else {
                    this.openModal('login-modal');
                }
            });
        }

        if (demoLoginBtn) {
            demoLoginBtn.addEventListener('click', () => {
                this.openModal('login-modal');
            });
        }
    }
};

document.addEventListener('DOMContentLoaded', () => Navigation.init());
window.Navigation = Navigation;
