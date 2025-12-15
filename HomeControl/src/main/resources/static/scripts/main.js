
let moradorIdEmEdicao = null;

// Toggle Password Visibility
function togglePassword(inputId) {
  const input = document.getElementById(inputId)
  const type = input.getAttribute("type") === "password" ? "text" : "password"
  input.setAttribute("type", type)
}

// Sidebar Toggle
function toggleSidebar() {
  const sidebar = document.getElementById("sidebar")
  const overlay = document.getElementById("sidebarOverlay")

  if (sidebar) {
    sidebar.classList.toggle("open")
  }
  if (overlay) {
    overlay.classList.toggle("open")
  }
}

// Modal Functions
function openModal(modalId) {
  const modal = document.getElementById(modalId)
  if (modal) {
    modal.classList.add("open")
    document.body.style.overflow = "hidden"
  }
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId)
  if (modal) {
    modal.classList.remove("open")
    document.body.style.overflow = ""
  }
}

// Close modal when clicking outside
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("modal-overlay")) {
    e.target.classList.remove("open")
    document.body.style.overflow = ""
  }
})

// Tab Switching
function switchTab(event, tabId) {
  // Remove active class from all triggers and contents
  document.querySelectorAll(".tabs-trigger").forEach((trigger) => {
    trigger.classList.remove("active")
  })
  document.querySelectorAll(".tabs-content").forEach((content) => {
    content.classList.remove("active")
  })

  // Add active class to clicked trigger and corresponding content
  event.target.classList.add("active")
  document.getElementById(tabId).classList.add("active")
}

// Dropdown Toggle
function toggleDropdown(button) {
  // Close all other dropdowns first
  document.querySelectorAll(".dropdown-menu.open").forEach((menu) => {
    if (menu !== button.nextElementSibling) {
      menu.classList.remove("open")
    }
  })

  const menu = button.nextElementSibling
  menu.classList.toggle("open")
}

// Close dropdowns when clicking outside
document.addEventListener("click", (e) => {
  if (!e.target.closest(".dropdown")) {
    document.querySelectorAll(".dropdown-menu.open").forEach((menu) => {
      menu.classList.remove("open")
    })
  }
})

// Bloco Accordion Toggle
function toggleBloco(header) {
  const card = header.closest(".bloco-card")
  card.classList.toggle("open")
}

// Toast Notifications
function showToast(message, type = "success") {
  let container = document.querySelector(".toast-container")
  if (!container) {
    container = document.createElement("div")
    container.className = "toast-container"
    document.body.appendChild(container)
  }

  const toast = document.createElement("div")
  toast.className = `toast toast-${type}`
  toast.innerHTML = `
    <span>${message}</span>
    <button class="toast-close" onclick="this.parentElement.remove()">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="18" y1="6" x2="6" y2="18"></line>
        <line x1="6" y1="6" x2="18" y2="18"></line>
      </svg>
    </button>
  `

  container.appendChild(toast)

  // Auto remove after 5 seconds
  setTimeout(() => {
    toast.remove()
  }, 5000)
}

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
  // Close sidebar on mobile when clicking a nav item
  document.querySelectorAll(".sidebar-nav-item").forEach((item) => {
    item.addEventListener("click", () => {
      if (window.innerWidth < 1024) {
        toggleSidebar()
      }
    })
  })
})

let tipoCadastroSelecionado = '';

function selectType(element, type) {

    document.querySelectorAll('.register-type-card').forEach(el => {
        el.classList.remove('selected');
    });

    element.classList.add('selected');
    tipoCadastroSelecionado = type;

    document.getElementById('moradorFields').style.display = type === 'morador' ? 'block' : 'none';
    document.getElementById('sindicoFields').style.display = type === 'sindico' ? 'block' : 'none';
}

function ObterRequisicao(event) {
    if(event) event.preventDefault();

    const nome = document.getElementById("nome").value;
    const email = document.getElementById("email").value;
    const telefone = document.getElementById("telefone").value;
    const senha = document.getElementById("password").value;
    const confirmarSenha = document.getElementById("confirmPassword").value;

    if (senha !== confirmarSenha) {
        alert("As senhas não coincidem!");
        return;
    }

    if (!tipoCadastroSelecionado) {
        alert("Por favor, selecione se é Morador ou Síndico.");
        return;
    }

    const dados = {
        nome: nome,
        email: email,
        telefone: telefone,
        senha: senha,
        tipoCadastro: tipoCadastroSelecionado
    };


    if (tipoCadastroSelecionado === 'morador') {
        dados.codigoCondominio = document.getElementById("codigoCondominio").value;
        dados.codigoUnidade = document.getElementById("codigoUnidade").value;

        if(!dados.codigoCondominio) {
            alert("Preencha o código do condomínio.");
            return;
        }
    } else if (tipoCadastroSelecionado === 'sindico') {
        dados.nomeCondominio = document.getElementById("nomeCondominio").value;
        dados.cnpj = document.getElementById("cnpj").value;
        dados.endereco = document.getElementById("endereco").value;

        if(!dados.nomeCondominio) {
            alert("Preencha o nome do condomínio.");
            return;
        }
    }

    // Envio
    fetch("http://localhost:8080/usuarios/cadastrar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dados)
    })
    .then(async response => {
        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(errorMessage);
        }
        return response.json();
    })
    .then(data => {
        alert("Cadastro realizado com sucesso! Redirecionando...");
        window.location.href = "index.html"; // Redireciona para login
    })
    .catch(error => {
        alert("Erro ao cadastrar: " + error.message);
        console.error(error);
    });
}

function goToStep2() {
    document.getElementById("step1").style.display = "none";
    document.getElementById("step1-indicator").classList.remove("active");

    document.getElementById("step2").style.display = "block";
    document.getElementById("step2-indicator").classList.add("active");
}

function goToStep1() {
    document.getElementById("step2").style.display = "none";
    document.getElementById("step2-indicator").classList.remove("active");

    document.getElementById("step1").style.display = "block";
    document.getElementById("step1-indicator").classList.add("active");
}

function realizarLogin(event) {
    // Impede que o formulário recarregue a página
    if(event) event.preventDefault();

    const email = document.getElementById('email').value;
    const senha = document.getElementById('password').value;

    // Envia as credenciais para o backend
    fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, senha: senha })
    })
    .then(async response => {
        if (response.ok) {
            const data = await response.json();

            // Opcional: Salvar dados não sensíveis para exibição
            localStorage.setItem("user_name", data.nome);
            localStorage.setItem("user_role", data.role);

            // SUCESSO: O servidor criou a sessão, agora podemos ir para o dashboard
            window.location.href = "dashboard.html";
        } else {
            // ERRO: Senha errada ou usuário inativo
            const erro = await response.text();
            alert("Erro ao entrar: " + erro);
        }
    })
    .catch(err => {
        console.error(err);
        alert("Erro de conexão com o servidor.");
    });
}


// --- FUNÇÕES DE MORADORES ---
function carregarMoradores() {
    const tbody = document.getElementById('tabelaMoradores');
    if (!tbody) return;

    fetch('/api/moradores')
        .then(res => res.json())
        .then(data => {
            tbody.innerHTML = '';
            data.forEach(m => {
                // Define estilos e textos baseados no status
                const statusClass = m.ativo ? 'badge-success' : 'badge-danger';
                const statusText = m.ativo ? 'Ativo' : 'Inativo';

                // Define o botão de ação (se está ativo, mostra Desativar, senão Ativar)
                const acaoTexto = m.ativo ? 'Desativar' : 'Ativar';
                const acaoCor = m.ativo ? 'danger' : ''; // Vermelho se for desativar

                const btnEditar = `
                    <button class="dropdown-item"
                        onclick="abrirModalEditar(${m.id}, '${m.nome}', '${m.email || ''}', '${m.telefone}', '${m.bloco}', '${m.unidade}')">
                        Editar
                    </button>
                `;

                const row = `
                    <tr>
                        <td>
                            <div style="display: flex; align-items: center; gap: 0.75rem;">
                                <div class="avatar avatar-sm">JS</div> <div><div style="font-weight: 500;">${m.nome}</div></div>
                            </div>
                        </td>
                        <td>${m.bloco} - Unidade ${m.unidade}</td>
                        <td>${m.telefone}</td>
                        <td><span class="badge badge-secondary">${m.perfil || 'Morador'}</span></td>
                        <td><span class="badge ${statusClass}">${statusText}</span></td>
                        <td>
                            <div class="dropdown">
                                <button class="btn btn-ghost btn-icon" onclick="toggleDropdown(this)">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="1"></circle><circle cx="12" cy="5" r="1"></circle><circle cx="12" cy="19" r="1"></circle></svg>
                                </button>
                                <div class="dropdown-menu">
                                    ${btnEditar}
                                    <div class="dropdown-divider"></div>
                                    <button class="dropdown-item ${acaoCor}" onclick="alterarStatusMorador(${m.id}, ${!m.ativo})">
                                        ${acaoTexto}
                                    </button>
                                </div>
                            </div>
                        </td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });
        });
}
// Helper para pegar iniciais
function getIniciais(nome) {
    return nome.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
}

// Função unificada de Salvar
function salvarNovoMorador() {
    const nome = document.getElementById('moradorNome').value;
    const email = document.getElementById('moradorEmail').value;
    const telefone = document.getElementById('moradorTelefone').value;
    const blocoSelect = document.getElementById('moradorBloco');
    const bloco = blocoSelect.options[blocoSelect.selectedIndex].text;
    const unidade = document.getElementById('moradorUnidade').value;
    const perfil = document.getElementById('moradorRole').value;

    const dados = {
        nome: nome,
        email: email,
        telefone: telefone,
        nomeBloco: bloco,
        numeroUnidade: parseInt(unidade),
        perfil: perfil
    };

    let url = '/api/moradores/cadastrar';
    let method = 'POST';

    // Se tiver ID, é edição
    if (moradorIdEmEdicao) {
        url = `/api/moradores/${moradorIdEmEdicao}`;
        method = 'PUT';
    }

    fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dados)
    })
    .then(async response => {
        if (response.ok) {
            alert(moradorIdEmEdicao ? 'Morador atualizado!' : 'Morador cadastrado!');
            closeModal('moradorModal');
            carregarMoradores(); // Recarrega a tabela
        } else {
            const erro = await response.text();
            alert('Erro: ' + erro);
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Falha na comunicação.');
    });
}

function alterarStatusMorador(id, novoStatus) {
    if(!confirm("Tem certeza que deseja alterar o status deste morador?")) return;

    fetch(`/api/moradores/${id}/status?ativo=${novoStatus}`, {
        method: 'PUT'
    })
    .then(response => {
        if(response.ok) {
            carregarMoradores();
        } else {
            alert("Erro ao alterar status");
        }
    });
}

// Inicializar quando o DOM estiver pronto
document.addEventListener("DOMContentLoaded", () => {
    // ... seus outros listeners ...
    carregarMoradores();
});
// Função chamada ao clicar em "Novo Morador"
function abrirModalNovoMorador() {
    moradorIdEmEdicao = null; // Limpa ID
    document.getElementById('modalTitulo').innerText = 'Novo Morador';

    // Limpa os campos
    document.getElementById('moradorNome').value = '';
    document.getElementById('moradorEmail').value = '';
    document.getElementById('moradorTelefone').value = '';
    document.getElementById('moradorUnidade').value = '';

    openModal('moradorModal');
}

// Função chamada ao clicar em "Editar" na tabela
function abrirModalEditar(id, nome, email, telefone, bloco, unidade) {
    moradorIdEmEdicao = id; // Define ID que será editado
    document.getElementById('modalTitulo').innerText = 'Editar Morador';

    // Preenche os campos com os dados recebidos
    document.getElementById('moradorNome').value = nome;
    document.getElementById('moradorEmail').value = email || '';
    document.getElementById('moradorTelefone').value = telefone;
    document.getElementById('moradorUnidade').value = unidade;

    // Selecionar o bloco no dropdown (lógica simples baseada no texto)
    const selectBloco = document.getElementById('moradorBloco');
    for (let i = 0; i < selectBloco.options.length; i++) {
        if (selectBloco.options[i].text === bloco) {
            selectBloco.selectedIndex = i;
            break;
        }
    }

    openModal('moradorModal');
}
