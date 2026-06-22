// State Management
const state = { 
    dashboard: null, 
    analytics: null, 
    topics: [], 
    problems: [], 
    revisions: null,
    user: null,
    isLogin: true
};

const $ = (id) => document.getElementById(id);

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    initializeAuth();
});

// ============ AUTH SYSTEM ============
function initializeAuth() {
    const storedUserId = localStorage.getItem('userId');
    const storedUserEmail = localStorage.getItem('userEmail');
    const storedUserName = localStorage.getItem('userName');
    
    if (storedUserId) {
        state.user = { userId: storedUserId, email: storedUserEmail, name: storedUserName };
        showApp();
    } else {
        showAuthPage();
    }
}

function showAuthPage() {
    $('auth-page').style.display = 'grid';
    $('app-container').style.display = 'none';
    setupAuthForm();
}

function showApp() {
    $('auth-page').style.display = 'none';
    $('app-container').style.display = 'grid';
    updateUserDisplay();
    setupNavigation();
    setupForms();
    setupFilters();
    $('refresh').addEventListener('click', loadAll);
    $('global-search').addEventListener('input', () => {
        const q = $('global-search').value;
        $('topic-query').value = q;
        $('problem-query').value = q;
        loadTopics();
        loadProblems();
    });
    $('logout-btn').addEventListener('click', logout);
    setTodayDefaults();
    loadAll();
}

function setupAuthForm() {
    const form = $('auth-form');
    const toggleBtn = $('toggle-form');
    const formTitle = $('form-title').querySelector('h2');
    const toggleText = $('toggle-text');
    const nameField = $('name-field');
    const submitBtn = $('auth-submit');
    
    state.isLogin = true;
    
    toggleBtn.addEventListener('click', (e) => {
        e.preventDefault();
        state.isLogin = !state.isLogin;
        
        if (state.isLogin) {
            formTitle.textContent = 'Login to Your Account';
            toggleText.textContent = "Don't have an account?";
            toggleBtn.textContent = 'Sign up here';
            nameField.style.display = 'none';
            submitBtn.textContent = 'Login';
        } else {
            formTitle.textContent = 'Create Your Account';
            toggleText.textContent = 'Already have an account?';
            toggleBtn.textContent = 'Login here';
            nameField.style.display = 'grid';
            submitBtn.textContent = 'Sign Up';
        }
        $('form-message').textContent = '';
        $('form-message').className = 'form-message';
    });
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = $('auth-email').value;
        const password = $('auth-password').value;
        const name = $('auth-name').value;
        const messageEl = $('form-message');
        
        try {
            const endpoint = state.isLogin ? '/api/auth/login' : '/api/auth/register';
            const body = state.isLogin 
                ? { email, password }
                : { email, password, name };
            
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            
            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Authentication failed');
            }
            
            const data = await response.json();
            
            // Store user info
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('userEmail', data.email);
            localStorage.setItem('userName', data.name);
            
            state.user = { userId: data.userId, email: data.email, name: data.name };
            
            messageEl.textContent = state.isLogin ? 'Login successful!' : 'Account created! Logging in...';
            messageEl.className = 'form-message success';
            
            setTimeout(() => showApp(), 500);
        } catch (error) {
            messageEl.textContent = error.message;
            messageEl.className = 'form-message error';
        }
    });
}

function logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userName');
    state.user = null;
    state.isLogin = true;
    $('auth-form').reset();
    $('form-message').textContent = '';
    $('form-message').className = 'form-message';
    showAuthPage();
}

function updateUserDisplay() {
    const initials = state.user.name ? state.user.name.split(' ').map(n => n[0]).join('').toUpperCase() : 'U';
    $('user-avatar').textContent = initials.slice(0, 1);
    $('user-name').textContent = state.user.name || state.user.email;
    $('user-email').textContent = state.user.email;
}

// ============ API CALLS ============
async function api(url, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        'X-User-Id': state.user.userId,
        ...options.headers
    };
    
    const response = await fetch(url, { headers, ...options });
    if (!response.ok) {
        if (response.status === 401) {
            logout();
            throw new Error('Session expired');
        }
        throw new Error(`Request failed: ${response.status}`);
    }
    return response.json();
}

// ============ APP FUNCTIONALITY ============
function setupNavigation() {
    document.querySelectorAll('.nav').forEach(button => {
        button.addEventListener('click', () => {
            document.querySelectorAll('.nav').forEach(nav => nav.classList.remove('active'));
            document.querySelectorAll('.page').forEach(page => page.classList.remove('active'));
            button.classList.add('active');
            $(button.dataset.page).classList.add('active');
            $('page-title').textContent = button.textContent.replace(/[^a-zA-Z\s]/g, '').trim();
        });
    });
}

function setupForms() {
    $('topic-form').addEventListener('submit', async (event) => {
        event.preventDefault();
        const data = Object.fromEntries(new FormData(event.target).entries());
        data.confidenceLevel = Number(data.confidenceLevel);
        await api('/api/topics', { method: 'POST', body: JSON.stringify(data) });
        event.target.reset();
        setTodayDefaults();
        toast('✅ Topic added and revision schedule generated.');
        loadAll();
    });

    $('problem-form').addEventListener('submit', async (event) => {
        event.preventDefault();
        const form = new FormData(event.target);
        const data = Object.fromEntries(form.entries());
        data.usedHint = form.has('usedHint');
        data.solvedIndependently = form.has('solvedIndependently');
        await api('/api/problems', { method: 'POST', body: JSON.stringify(data) });
        event.target.reset();
        setTodayDefaults();
        toast('✅ Problem added and revision schedule generated.');
        loadAll();
    });
}

function setupFilters() {
    ['topic-query', 'topic-category', 'topic-date'].forEach(id => $(id).addEventListener('input', loadTopics));
    ['problem-query', 'problem-tag', 'problem-platform', 'problem-difficulty', 'problem-date'].forEach(id => $(id).addEventListener('input', loadProblems));
}

function setTodayDefaults() {
    const today = new Date().toISOString().slice(0, 10);
    const dateLearnedField = document.querySelector('[name="dateLearned"]');
    const solvedDateField = document.querySelector('[name="solvedDate"]');
    if (dateLearnedField) dateLearnedField.value = today;
    if (solvedDateField) solvedDateField.value = today;
}

async function loadAll() {
    await Promise.all([loadDashboard(), loadTopics(), loadProblems(), loadRevisions(), loadAnalytics()]);
}

async function loadDashboard() {
    state.dashboard = await api('/api/dashboard');
    $('overall-readiness').textContent = `${state.dashboard.overallReadiness}%`;
    $('current-streak').textContent = state.dashboard.streaks.currentStreak;
    $('topics-count').textContent = state.dashboard.topicsLearned;
    $('problems-count').textContent = state.dashboard.problemsSolved;
    $('revision-time').textContent = `${state.dashboard.estimatedRevisionMinutes}m`;
    $('today-badge').textContent = `${state.dashboard.today.length} due`;
    $('overdue-badge').textContent = `${state.dashboard.overdue.length} late`;
    renderRevisionList('today-list', state.dashboard.today);
    renderRevisionList('overdue-list', state.dashboard.overdue);
    renderBars('readiness-bars', state.dashboard.readiness, '%');
}

async function loadTopics() {
    const params = new URLSearchParams();
    if ($('topic-query').value) params.set('q', $('topic-query').value);
    if ($('topic-category').value) params.set('category', $('topic-category').value);
    if ($('topic-date').value) params.set('date', $('topic-date').value);
    state.topics = await api(`/api/topics?${params}`);
    $('topics-table').innerHTML = table(['Topic', 'Category', 'Learned', 'Confidence', 'Last Revised', 'Notes'],
        state.topics.map(t => [t.name, t.category, t.dateLearned, stars(t.confidenceLevel), t.lastRevised, t.notes]));
}

async function loadProblems() {
    const params = new URLSearchParams();
    if ($('problem-query').value) params.set('q', $('problem-query').value);
    if ($('problem-tag').value) params.set('tag', $('problem-tag').value);
    if ($('problem-platform').value) params.set('platform', $('problem-platform').value);
    if ($('problem-difficulty').value) params.set('difficulty', $('problem-difficulty').value);
    if ($('problem-date').value) params.set('date', $('problem-date').value);
    state.problems = await api(`/api/problems?${params}`);
    $('problems-table').innerHTML = table(['Platform', 'No.', 'Problem', 'Difficulty', 'Tags', 'Solved', 'Hint', 'Solo'],
        state.problems.map(p => [p.platform, p.problemNumber, p.name, p.difficulty, p.topicTags, p.solvedDate, yesNo(p.usedHint), yesNo(p.solvedIndependently)]));
}

async function loadRevisions() {
    state.revisions = await api('/api/revisions/today');
    const items = [...state.revisions.overdue, ...state.revisions.today];
    $('revision-board').innerHTML = items.length ? items.map(revisionCard).join('') : empty('No revisions due right now.');
    document.querySelectorAll('[data-complete]').forEach(button => {
        button.addEventListener('click', async () => {
            const card = button.closest('.item');
            const rating = Number(button.dataset.rating);
            const recall = button.dataset.recall;
            await api(`/api/revisions/${button.dataset.complete}/complete`, {
                method: 'POST',
                body: JSON.stringify({ rating, recallLevel: recall, notes: `Marked as ${recall}` })
            });
            card.querySelector('.notes').classList.add('visible');
            toast('✅ Revision completed and next date rescheduled.');
            setTimeout(loadAll, 500);
        });
    });
}

async function loadAnalytics() {
    state.analytics = await api('/api/analytics');
    drawChart('problems-chart', state.analytics.problemsPerMonth, '#667eea');
    drawChart('topics-chart', state.analytics.topicsPerMonth, '#764ba2');
    renderBars('tag-bars', state.analytics.tagDistribution, '');
    $('weak-list').innerHTML = state.analytics.weakAreas.map(w => item(w.tag, `${w.count} solved`, w.recommendation)).join('');
    $('forgotten-list').innerHTML = state.analytics.forgottenTopics.length
        ? state.analytics.forgottenTopics.map(f => item(f.topic, `${f.risk}: last revised ${f.daysSinceRevision} days ago`, f.category)).join('')
        : empty('No forgotten topics beyond 30 days.');
    $('recommendations').innerHTML = state.analytics.recommendations.map(r => `<span class="chip">${escapeHtml(r)}</span>`).join('');
    renderHeatmap(state.analytics.heatmap);
}

// ============ RENDERING HELPERS ============
function renderRevisionList(id, revisions) {
    $(id).innerHTML = revisions.length 
        ? revisions.map(r => item(r.title, `${r.subtitle} · due ${r.dueDate}`, r.daysLate ? `⚠️ ${r.daysLate} days late` : '✅ Due today')).join('') 
        : empty('Clear for now.');
}

function revisionCard(r) {
    return `<article class="item">
        <div class="item-head">
            <div><strong>${escapeHtml(r.title)}</strong><small>${escapeHtml(r.subtitle)} · last due ${r.dueDate}</small></div>
            <span class="pill">${r.itemType}</span>
        </div>
        <p class="muted">Solve or recall this before opening your notes.</p>
        <div class="revision-actions">
            <button data-complete="${r.revisionId}" data-rating="5" data-recall="Easy Recall">✅ I solved it</button>
            <button data-complete="${r.revisionId}" data-rating="3" data-recall="Partial Recall" class="secondary">⚡ Partial recall</button>
            <button data-complete="${r.revisionId}" data-rating="1" data-recall="Forgot Completely" class="secondary">❌ Forgot</button>
        </div>
        <div class="notes"><strong>📝 Notes</strong><br>${escapeHtml(r.notes || 'No notes saved.')}</div>
    </article>`;
}

function renderBars(id, data, suffix) {
    const entries = Object.entries(data || {}).sort((a, b) => b[1] - a[1]);
    const max = Math.max(1, ...entries.map(([, value]) => Number(value)));
    $(id).innerHTML = entries.map(([label, value]) => {
        const width = Math.max(4, Number(value) / max * 100);
        return `<div class="bar-row"><strong>${escapeHtml(label)}</strong><div class="bar-track"><div class="bar-fill" style="width:${width}%"></div></div><span>${value}${suffix}</span></div>`;
    }).join('');
}

function renderHeatmap(data) {
    const now = new Date();
    const cells = [];
    for (let i = 181; i >= 0; i--) {
        const d = new Date(now);
        d.setDate(now.getDate() - i);
        const key = d.toISOString().slice(0, 10);
        const count = Number(data[key] || 0);
        const level = count >= 4 ? 4 : count;
        cells.push(`<span class="day level-${level}" title="${key}: ${count} activities"></span>`);
    }
    $('heatmap').innerHTML = cells.join('');
}

function drawChart(id, data, color) {
    const canvas = $(id);
    const ctx = canvas.getContext('2d');
    const rect = canvas.getBoundingClientRect();
    canvas.width = Math.max(320, rect.width * devicePixelRatio);
    canvas.height = 180 * devicePixelRatio;
    ctx.scale(devicePixelRatio, devicePixelRatio);
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    const entries = Object.entries(data || {});
    const max = Math.max(1, ...entries.map(([, v]) => Number(v)));
    const barWidth = entries.length ? (rect.width - 30) / entries.length : 40;
    ctx.fillStyle = color;
    entries.forEach(([label, value], index) => {
        const height = Number(value) / max * 120;
        const x = 20 + index * barWidth;
        const y = 140 - height;
        ctx.fillRect(x, y, Math.max(16, barWidth - 12), height);
        ctx.fillStyle = '#64748b';
        ctx.font = '11px sans-serif';
        ctx.fillText(label.slice(5), x, 160);
        ctx.fillStyle = color;
    });
}

function table(headers, rows) {
    if (!rows.length) return empty('No records found.');
    return `<table><thead><tr>${headers.map(h => `<th>${h}</th>`).join('')}</tr></thead><tbody>${rows.map(row => `<tr>${row.map(cell => `<td>${escapeHtml(String(cell ?? ''))}</td>`).join('')}</tr>`).join('')}</tbody></table>`;
}

function item(title, subtitle, detail) {
    const tone = String(subtitle).includes('High') ? 'danger' : String(subtitle).includes('Medium') || String(subtitle).includes('Watch') ? 'warning' : '';
    return `<article class="item"><strong>${escapeHtml(title)}</strong><small class="${tone}">${escapeHtml(subtitle)}</small><div class="muted">${escapeHtml(detail || '')}</div></article>`;
}

function empty(message) { return `<p class="muted">${message}</p>`; }
function stars(value) { return '★★★★★'.slice(0, Number(value || 0)); }
function yesNo(value) { return value ? '✅' : '❌'; }
function escapeHtml(value) {
    return value.replace(/[&<>"']/g, ch => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[ch]));
}

function toast(message) {
    $('toast').textContent = message;
    $('toast').classList.add('visible');
    setTimeout(() => $('toast').classList.remove('visible'), 2400);
}
