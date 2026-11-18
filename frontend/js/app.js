// Se o frontend for servido pelo próprio Spring Boot (pasta static), deixe ''.
const API = 'http://localhost:8080';
const BASE = '/api.github';
const $ = s => document.querySelector(s);
const app = $('#app');
const state = { user: null, tab: 'perfil', cache: {}, repo: null };
const COLORS = ['#f1e05a','#3178c6','#e34c26','#b07219','#563d7c','#00ADD8','#A97BFF','#89e051','#f34b7d','#c6538c'];

const esc = v => String(v ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const num = n => Number(n ?? 0).toLocaleString('pt-BR');
const avatar = u => `https://github.com/${encodeURIComponent(u)}.png?size=96`;
const q = o => new URLSearchParams(o).toString();

async function api(path, params = {}, method = 'GET') {
  let res;
  try { res = await fetch(`${API}${BASE}${path}?${q(params)}`, { method }); }
  catch (e) { throw new Error('Não foi possível conectar ao backend. Verifique se ele está rodando em ' + API + ' e se o CORS está liberado.'); }
  if (!res.ok) {
    let t = ''; try { const j = await res.json(); t = j.message || j.erro || j.error || JSON.stringify(j); } catch {}
    throw new Error(`Erro ${res.status}${t ? ': ' + t : ''}`);
  }
  return res.json();
}

const loading = () => '<div class="spin" role="status" aria-label="Carregando"></div>';
const errBox = m => `<div class="msg err">${esc(m)}</div>`;
const empty = m => `<div class="empty">${esc(m)}</div>`;

/* ---------- Home ---------- */
function home() {
  state.user = null; state.repo = null; state.cache = {};
  $('#topSearch').style.display = 'none';
  window.scrollTo(0, 0);
  app.innerHTML = `
    <section class="hero">
      <h1>Analise qualquer perfil do GitHub</h1>
      <p>Veja repositórios, atividade, linguagens, rede de contatos e o histórico de mudanças de um usuário.</p>
      <form class="searchbar" onsubmit="return go(event,'home')">
        <input type="text" id="homeInput" placeholder="Nome de usuário do GitHub" aria-label="Nome de usuário" maxlength="39" autofocus>
        <button class="btn primary" type="submit">Analisar perfil</button>
        <button class="btn" type="button" onclick="searchUsers()">Procurar usuários</button>
      </form>
      <div class="hint">
        <div class="card"><h3>Perfil e contribuições</h3><p>Números gerais, commits, issues e pull requests.</p></div>
        <div class="card"><h3>Repositórios</h3><p>Lista completa e detalhes de cada projeto.</p></div>
        <div class="card"><h3>Histórico</h3><p>Sincronize e compare como o perfil mudou.</p></div>
      </div>
    </section><div id="homeResult"></div>`;
}

function go(e, from) {
  e.preventDefault();
  const v = $(from === 'top' ? '#topInput' : '#homeInput').value.trim();
  if (!v) return false;
  openUser(v); return false;
}

/* ---------- Busca de usuários ---------- */
async function searchUsers(page = 1) {
  const termo = $('#homeInput').value.trim();
  const out = $('#homeResult');
  if (!termo) { out.innerHTML = errBox('Digite um nome para procurar.'); return; }
  out.innerHTML = loading();
  try {
    const d = await api('/users/multiplos', { nome: termo, pagina: page, tamanho: 30 });
    out.innerHTML = `<h3 style="margin:0 0 12px">Resultados para “${esc(termo)}”</h3>` +
      (d.conteudo.length ? usersGrid(d.conteudo) + pager(d, 'searchUsers') : empty('Nenhum usuário encontrado.'));
  } catch (e) { out.innerHTML = errBox(e.message); }
}
const usersGrid = list => `<div class="users">${list.map(u =>
  `<div class="ucard" tabindex="0" role="button" onclick="openUser('${esc(u.userName)}')" onkeydown="if(event.key==='Enter')openUser('${esc(u.userName)}')"><img src="${avatar(u.userName)}" alt="" loading="lazy"><span>${esc(u.userName)}</span></div>`).join('')}</div>`;
function pager(d, fn, extra = '') {
  return `<div class="pager"><button class="btn" ${d.pagina <= 1 ? 'disabled' : ''} onclick="${fn}(${d.pagina - 1}${extra})">Anterior</button>
  <span>Página ${d.pagina}${d.total != null ? ` · ${num(d.total)} no total` : ''}</span>
  <button class="btn" ${d.temProxima ? '' : 'disabled'} onclick="${fn}(${d.pagina + 1}${extra})">Próxima</button></div>`;
}

/* ---------- Usuário ---------- */
async function openUser(name) {
  state.user = null; state.tab = 'perfil'; state.repo = null; state.cache = {};
  $('#topSearch').style.display = 'flex'; $('#topInput').value = '';
  window.scrollTo(0, 0);
  app.innerHTML = loading();
  try {
    state.user = await api('/users/unico', { nome: name });
    renderUser();
  } catch (e) {
    app.innerHTML = errBox(e.message) + `<button class="btn back" onclick="home()">Voltar ao início</button>`;
  }
}

const TABS = [['perfil','Perfil'],['repos','Repositórios'],['eventos','Eventos'],['seguidores','Seguidores'],['seguindo','Seguindo'],['historico','Histórico']];
function renderUser() {
  const u = state.user;
  app.innerHTML = `
    <div class="profile">
      <img class="avatar" src="${avatar(u.userName)}" alt="Foto de ${esc(u.userName)}">
      <div>
        <h2>${esc(u.nome || u.userName)} <span class="login">${esc(u.userName)}</span></h2>
        <div class="bio">${esc(u.bio || 'Sem biografia.')}</div>
        <span class="pill">Linguagem preferida: ${esc(u.linguagemPreferida || 'N/A')}</span>
        <span class="pill">Conta criada em ${esc(u.criadoEm || '-')}</span>
        <a class="small" href="https://github.com/${encodeURIComponent(u.userName)}" target="_blank" rel="noopener">Abrir no GitHub</a>
      </div>
    </div>
    <nav class="tabs" role="tablist">${TABS.map(([k, l]) => `<button class="tab ${state.tab === k ? 'on' : ''}" role="tab" onclick="setTab('${k}')">${l}</button>`).join('')}</nav>
    <div id="tabBody"></div>`;
  loadTab();
}
function setTab(k) { state.tab = k; state.repo = null; renderUser(); }

function loadTab(page = 1) {
  const fn = { perfil, repos, eventos, seguidores: p => rede('seguidores', p), seguindo: p => rede('seguindo', p), historico }[state.tab];
  fn(page);
}
const body = () => $('#tabBody');

/* Perfil */
async function perfil() {
  const u = state.user;
  const stats = [['Repositórios', u.repositorios],['Estrelas', u.estrelas],['Seguidores', u.seguidores],['Seguindo', u.seguindo]];
  body().innerHTML = `<div class="grid g4">${stats.map(([l, n]) => `<div class="card stat"><div class="n">${num(n)}</div><div class="l">${l}</div></div>`).join('')}</div>
  <div class="grid g3" style="margin-top:16px"><div class="card" id="contrib">${loading()}</div><div class="card" id="lang">${loading()}</div></div>`;
  api('/users/contribuicoes', { nome: u.userName }).then(c => {
    const items = [['Contribuições', c.contribuicoes, 'var(--green)'],['Commits', c.commits, 'var(--blue)'],['Issues', c.issues, 'var(--yellow)'],['Pull requests', c.pullRequests, 'var(--purple)']];
    const max = Math.max(1, ...items.map(i => i[1] || 0));
    $('#contrib').innerHTML = '<h3>Contribuições no último ano</h3>' + items.map(([l, n, c]) =>
      `<div class="bar"><span class="nm">${l}</span><div class="tr"><div class="fl" style="width:${(n / max) * 100}%;background:${c}"></div></div><b>${num(n)}</b></div>`).join('');
  }).catch(e => $('#contrib').innerHTML = errBox(e.message));
  api('/users/rankingLinguagens', { nome: u.userName }).then(r => {
    const l = (r.listaLinguagens || []);
    $('#lang').innerHTML = '<h3>Ranking de linguagens</h3>' + (l.length ? l.map((x, i) =>
      `<div class="bar"><span class="nm">${i + 1}. ${esc(x)}</span><div class="tr"><div class="fl" style="width:${100 - i * (80 / Math.max(l.length, 1))}%;background:${COLORS[i % COLORS.length]}"></div></div></div>`).join('')
      + '<div class="small">Ordenadas da mais usada para a menos usada.</div>' : '<div class="small">Sem dados.</div>');
  }).catch(e => $('#lang').innerHTML = errBox(e.message));
}

/* Repositórios */
async function repos() {
  if (state.repo) return repoDetail();
  body().innerHTML = loading();
  try {
    const list = state.cache.repos ||= await api('/repos/multiplos', { usuario: state.user.userName });
    if (!list.length) return body().innerHTML = empty('Este usuário não tem repositórios públicos.');
    body().innerHTML = `<div class="small" style="margin-bottom:8px">${list.length} repositórios. Clique em um para ver os detalhes.</div><div class="list">${list.map((r, i) => `
      <div class="row click" tabindex="0" onclick="pickRepo(${i})" onkeydown="if(event.key==='Enter')pickRepo(${i})">
        <h4>${esc(r.nome)}</h4>
        <p>${r.descricao && r.descricao !== 'N/A' ? esc(r.descricao) : 'Sem descrição.'}</p>
        <div>${(r.topicos || []).map(t => `<span class="pill topic">${esc(t)}</span>`).join('')}</div>
        <div class="meta">
          <span><span class="dot" style="background:${r.linguagem && r.linguagem !== 'N/A' ? COLORS[Math.abs(hash(r.linguagem)) % COLORS.length] : 'var(--border)'}"></span>${esc(r.linguagem || 'N/A')}</span>
          <span>★ ${num(r.estrelas)}</span><span>Forks ${num(r.forks)}</span><span>Issues abertas ${num(r.openIssues)}</span>
          <span>Atualizado em ${esc(r.ultimoUpdate)}</span><span>Criado em ${esc(r.criadoEm)}</span>
        </div></div>`).join('')}</div>`;
  } catch (e) { body().innerHTML = errBox(e.message); }
}
const hash = s => [...s].reduce((a, c) => (a << 5) - a + c.charCodeAt(0) | 0, 0);
function pickRepo(i) { state.repo = state.cache.repos[i].nome; repos(); window.scrollTo(0, 0); }

async function repoDetail() {
  body().innerHTML = `<button class="btn back" onclick="state.repo=null;repos()">Voltar para repositórios</button>${loading()}`;
  try {
    const r = await api('/repos/unico', { usuario: state.user.userName, repositorio: state.repo });
    const st = [['Commits', r.commits],['Branches', r.branches],['Issues', r.issues],['Pull requests', r.pullsRequests],['Forks', r.forks],['Estrelas', r.estrelas]];
    body().innerHTML = `<button class="btn back" onclick="state.repo=null;repos()">Voltar para repositórios</button>
      <div class="card" style="margin-bottom:16px"><h2 style="margin:0 0 4px;font-size:22px">${esc(r.nome)}</h2>
        <div class="bio">${esc(r.descricao && r.descricao !== 'N/A' ? r.descricao : 'Sem descrição.')}</div>
        <div>${(r.topicos || []).map(t => `<span class="pill topic">${esc(t)}</span>`).join('')}</div></div>
      <div class="grid g4">${st.map(([l, n]) => `<div class="card stat"><div class="n">${num(n)}</div><div class="l">${l}</div></div>`).join('')}</div>
      <div class="grid g3" style="margin-top:16px">
        <div class="card"><h3>Linguagens</h3><div class="small" style="margin-bottom:8px">Principal: <b style="color:var(--text)">${esc(r.linguagemPrincipal || 'N/A')}</b></div>
          ${(r.linguagensUsadas || []).map(l => `<span class="pill"><span class="dot" style="background:${COLORS[Math.abs(hash(l)) % COLORS.length]}"></span>${esc(l)}</span>`).join('') || '<span class="small">Sem dados.</span>'}</div>
        <div class="card"><h3>Contribuidores</h3>${(r.contribuidores || []).length ? usersGrid((r.contribuidores).map(c => ({ userName: c.login }))) : '<span class="small">Sem dados.</span>'}</div>
      </div>`;
  } catch (e) { body().innerHTML = `<button class="btn back" onclick="state.repo=null;repos()">Voltar para repositórios</button>` + errBox(e.message); }
}

/* Eventos */
const EV = { PushEvent: 'Push', PullRequestEvent: 'Pull request', IssuesEvent: 'Issue', CreateEvent: 'Criação', WatchEvent: 'Estrela', ForkEvent: 'Fork', DeleteEvent: 'Exclusão', IssueCommentEvent: 'Comentário' };
async function eventos(page = 1) {
  body().innerHTML = loading();
  try {
    const d = await api('/users/eventos', { nome: state.user.userName, pagina: page, tamanho: 30 });
    body().innerHTML = d.conteudo.length ? `<div class="list">${d.conteudo.map(e => `
      <div class="row"><div class="meta" style="justify-content:space-between"><span><span class="tag ${esc(e.tipo)}">${esc(EV[e.tipo] || e.tipo)}</span>
      &nbsp; <b style="color:var(--text)">${esc(e.repositorioNome)}</b></span><span>${esc(e.criadoEm)}</span></div></div>`).join('')}</div>` + pager(d, 'eventos')
      : empty('Nenhum evento recente encontrado para este usuário.');
  } catch (e) { body().innerHTML = errBox(e.message); }
}

/* Seguidores / Seguindo */
async function rede(tipo, page = 1) {
  body().innerHTML = loading();
  try {
    const d = await api('/users/' + tipo, { nome: state.user.userName, pagina: page, tamanho: 30 });
    body().innerHTML = d.conteudo.length ? usersGrid(d.conteudo) + pager(d, 'loadTab') : empty(tipo === 'seguidores' ? 'Ninguém segue este usuário ainda.' : 'Este usuário não segue ninguém.');
  } catch (e) { body().innerHTML = errBox(e.message); }
}

/* Histórico */
async function historico(page = 1) {
  const u = state.user.userName;
  body().innerHTML = `<div class="card" style="display:flex;gap:12px;align-items:center;justify-content:space-between;flex-wrap:wrap;margin-bottom:16px">
    <div><b>Sincronizar dados</b><div class="small">Salva uma foto atual do perfil para comparar com as próximas.</div></div>
    <button class="btn primary" id="syncBtn" onclick="sync()">Sincronizar agora</button></div>
    <div id="syncMsg"></div><div id="hist">${loading()}</div>`;
  try {
    const d = await api('/analisar/historico', { nome: u, pagina: page, tamanho: 30 });
    const snaps = d.flatMap(x => x.informacoesAntigas || []);
    $('#hist').innerHTML = snaps.length ? `<div class="tl">${snaps.map(s => `
      <div class="item card"><div style="display:flex;justify-content:space-between;flex-wrap:wrap;gap:8px"><b>${esc(s.sincronizadoEm)}</b><span class="small">${esc(s.nome)}</span></div>
      <div class="meta" style="margin-top:8px"><span>Repositórios ${num(s.repositorios)}</span><span>Seguidores ${num(s.seguidores)}</span><span>Seguindo ${num(s.seguindo)}</span><span>Estrelas ${num(s.estrelas)}</span>
      <span>Contribuições ${num(s.contribuicoes)}</span><span>Commits ${num(s.commits)}</span><span>Issues ${num(s.issues)}</span><span>PRs ${num(s.pullRequests)}</span></div>
      <div class="diff"><span class="pill add">+${num(s.repositoriosAdicionados)} adicionados</span><span class="pill upd">${num(s.repositoriosAtualizados)} atualizados</span><span class="pill rem">−${num(s.repositoriosRemovidos)} removidos</span>
      <span class="pill">Tabela de usuários alterada: ${esc(s.alterouTabelaUsuarios)}</span><span class="pill">Tabela de repositórios alterada: ${esc(s.alterouTabelaRepositorios)}</span></div></div>`).join('')}</div>`
      : empty('Ainda não há histórico. Clique em “Sincronizar agora” para criar o primeiro registro.');
  } catch (e) { $('#hist').innerHTML = errBox(e.message); }
}
async function sync() {
  const b = $('#syncBtn'); b.disabled = true; b.textContent = 'Sincronizando...';
  try {
    const u = await api('/analisar/sincronizar', { nome: state.user.userName }, 'POST');
    state.user = u;
    $('#syncMsg').innerHTML = '<div class="msg ok">Dados sincronizados com sucesso.</div>';
    const h = $('#hist'); h.innerHTML = loading();
    const d = await api('/analisar/historico', { nome: u.userName, pagina: 1, tamanho: 30 });
    historico();
  } catch (e) { $('#syncMsg').innerHTML = errBox(e.message); }
  b.disabled = false; b.textContent = 'Sincronizar agora';
}

home();
