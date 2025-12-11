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

function realizarLogin() {
    const email = document.getElementById('email').value;
    const senha = document.getElementById('password').value;

    fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, senha: senha })
    })
    .then(async response => {
        if (response.ok) {
            const data = await response.json();
            // Opcional: Salvar no localStorage apenas para exibir nome na tela,
            // mas a segurança real está no Cookie da sessão que o navegador gerencia sozinho.
            localStorage.setItem("user_name", data.nome);
            localStorage.setItem("user_role", data.role);

            window.location.href = "dashboard.html";
        } else {
            const erro = await response.text();
            alert("Erro ao entrar: " + erro);
        }
    })
    .catch(err => console.error(err));
}

function salvarNovoMorador() {
    // Pegar os valores do modal
    // Nota: Adicione IDs aos inputs do modal se não tiverem (ex: id="modalNome", id="modalEmail")
    // Baseado na sua imagem, assumindo que você colocará os IDs:

    const nome = document.getElementById('moradorNome').value;
    const email = document.getElementById('moradorEmail').value;
    const telefone = document.getElementById('moradorTelefone').value;
    const bloco = document.getElementById('moradorBloco').value; // O select
    const unidade = document.getElementById('moradorUnidade').value; // O input text
    const perfil = document.getElementById('moradorRole').value;

    // TODO: Em um app real, você pega esse ID da sessão/login do síndico
    // Para teste, coloque o ID do condomínio que você criou manualmente no banco ou viu no debug
    const idCondominio = 1;

    const dados = {
        nome: nome,
        email: email,
        telefone: telefone,
        nomeBloco: bloco, // Enviando o texto "Bloco A", por exemplo
        numeroUnidade: parseInt(unidade),
        perfil: perfil
    };

    fetch(`http://localhost:8080/api/moradores/cadastrar?idCondominio=${idCondominio}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(dados)
    })
    .then(async response => {
        if (response.ok) {
            alert('Morador cadastrado com sucesso!');
            closeModal('moradorModal');
            window.location.reload(); // Recarrega para mostrar na tabela (se já tiver listagem)
        } else {
            const erro = await response.text();
            alert('Erro: ' + erro);
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Falha na comunicação com o servidor.');
    });
}



