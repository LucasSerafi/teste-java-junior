// Configuração da API
const API_BASE_URL = '/api/products';

// Elementos do DOM
const produtoForm = document.getElementById('produto-form');
const produtoIdInput = document.getElementById('produto-id');
const nomeInput = document.getElementById('nome');
const descricaoInput = document.getElementById('descricao');
const precoInput = document.getElementById('preco');
const quantidadeInput = document.getElementById('quantidade');
const submitBtn = document.getElementById('submit-btn');
const cancelBtn = document.getElementById('cancel-btn');
const formTitle = document.getElementById('form-title');

const searchInput = document.getElementById('search-input');
const searchBtn = document.getElementById('search-btn');
const showAllBtn = document.getElementById('show-all-btn');
const lowStockBtn = document.getElementById('low-stock-btn');

const produtosTbody = document.getElementById('produtos-tbody');
const loadingDiv = document.getElementById('loading');
const errorMessageDiv = document.getElementById('error-message');

// Estado da aplicação
let editingProductId = null;

// Inicialização
document.addEventListener('DOMContentLoaded', function() {
    carregarProdutos();
    setupEventListeners();
});

// Configurar event listeners
function setupEventListeners() {
    produtoForm.addEventListener('submit', handleFormSubmit);
    cancelBtn.addEventListener('click', cancelarEdicao);
    searchBtn.addEventListener('click', buscarProdutos);
    showAllBtn.addEventListener('click', carregarProdutos);
    lowStockBtn.addEventListener('click', buscarEstoqueBaixo);
    
    // Busca em tempo real
    searchInput.addEventListener('keyup', function(e) {
        if (e.key === 'Enter') {
            buscarProdutos();
        }
    });
}

// Funções de API
async function apiRequest(url, options = {}) {
    try {
        showLoading(true);
        hideError();
        
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `Erro HTTP: ${response.status}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        
        return null;
    } catch (error) {
        showError(error.message);
        throw error;
    } finally {
        showLoading(false);
    }
}

// Carregar todos os produtos
async function carregarProdutos() {
    try {
        const produtos = await apiRequest(API_BASE_URL);
        renderizarProdutos(produtos);
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
    }
}

// Buscar produtos por nome
async function buscarProdutos() {
    const nome = searchInput.value.trim();
    if (!nome) {
        carregarProdutos();
        return;
    }
    
    try {
        const produtos = await apiRequest(`${API_BASE_URL}/search?nome=${encodeURIComponent(nome)}`);
        renderizarProdutos(produtos);
    } catch (error) {
        console.error('Erro ao buscar produtos:', error);
    }
}

// Buscar produtos com estoque baixo
async function buscarEstoqueBaixo() {
    try {
        const produtos = await apiRequest(`${API_BASE_URL}/low-stock?quantidade=10`);
        renderizarProdutos(produtos);
    } catch (error) {
        console.error('Erro ao buscar produtos com estoque baixo:', error);
    }
}

// Renderizar lista de produtos
function renderizarProdutos(produtos) {
    produtosTbody.innerHTML = '';
    
    if (!produtos || produtos.length === 0) {
        produtosTbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 40px; color: #666;">
                    Nenhum produto encontrado
                </td>
            </tr>
        `;
        return;
    }
    
    produtos.forEach(produto => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${produto.id}</td>
            <td>${produto.nome}</td>
            <td>${produto.descricao || '-'}</td>
            <td>R$ ${produto.preco.toFixed(2).replace('.', ',')}</td>
            <td>${produto.quantidade}</td>
            <td>
                <div class="action-buttons">
                    <button onclick="editarProduto(${produto.id})" class="success">Editar</button>
                    <button onclick="deletarProduto(${produto.id})" class="danger">Excluir</button>
                </div>
            </td>
        `;
        produtosTbody.appendChild(row);
    });
}

// Manipular envio do formulário
async function handleFormSubmit(e) {
    e.preventDefault();
    
    const produto = {
        nome: nomeInput.value.trim(),
        descricao: descricaoInput.value.trim(),
        preco: parseFloat(precoInput.value),
        quantidade: parseInt(quantidadeInput.value)
    };
    
    try {
        if (editingProductId) {
            await apiRequest(`${API_BASE_URL}/${editingProductId}`, {
                method: 'PUT',
                body: JSON.stringify(produto)
            });
            showSuccess('Produto atualizado com sucesso!');
        } else {
            await apiRequest(API_BASE_URL, {
                method: 'POST',
                body: JSON.stringify(produto)
            });
            showSuccess('Produto criado com sucesso!');
        }
        
        limparFormulario();
        carregarProdutos();
    } catch (error) {
        console.error('Erro ao salvar produto:', error);
    }
}

// Editar produto
async function editarProduto(id) {
    try {
        const produto = await apiRequest(`${API_BASE_URL}/${id}`);
        
        produtoIdInput.value = produto.id;
        nomeInput.value = produto.nome;
        descricaoInput.value = produto.descricao || '';
        precoInput.value = produto.preco;
        quantidadeInput.value = produto.quantidade;
        
        editingProductId = id;
        formTitle.textContent = 'Editar Produto';
        submitBtn.textContent = 'Atualizar Produto';
        cancelBtn.style.display = 'inline-block';
        
        // Scroll para o formulário
        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Erro ao carregar produto para edição:', error);
    }
}

// Deletar produto
async function deletarProduto(id) {
    if (!confirm('Tem certeza que deseja excluir este produto?')) {
        return;
    }
    
    try {
        await apiRequest(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });
        showSuccess('Produto excluído com sucesso!');
        carregarProdutos();
    } catch (error) {
        console.error('Erro ao deletar produto:', error);
    }
}

// Cancelar edição
function cancelarEdicao() {
    limparFormulario();
}

// Limpar formulário
function limparFormulario() {
    produtoForm.reset();
    produtoIdInput.value = '';
    editingProductId = null;
    formTitle.textContent = 'Adicionar Produto';
    submitBtn.textContent = 'Adicionar Produto';
    cancelBtn.style.display = 'none';
}

// Funções de UI
function showLoading(show) {
    loadingDiv.style.display = show ? 'block' : 'none';
}

function showError(message) {
    errorMessageDiv.textContent = message;
    errorMessageDiv.style.display = 'block';
    setTimeout(() => {
        hideError();
    }, 5000);
}

function hideError() {
    errorMessageDiv.style.display = 'none';
}

function showSuccess(message) {
    // Remover mensagem anterior se existir
    const existingMessage = document.querySelector('.success-message');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    // Criar nova mensagem
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message';
    successDiv.textContent = message;
    
    // Inserir no início do container principal
    const main = document.querySelector('main');
    main.insertBefore(successDiv, main.firstChild);
    
    // Remover após 3 segundos
    setTimeout(() => {
        successDiv.remove();
    }, 3000);
}

