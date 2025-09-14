const API_BASE_URL = 'http://localhost:8081/api/products';
const CATEGORIA_API_BASE_URL = 'http://localhost:8081/api/categorias';
const produtoForm = document.getElementById('produto-form');
const produtoIdInput = document.getElementById('produto-id');
const nomeInput = document.getElementById('nome');
const descricaoInput = document.getElementById('descricao');
const precoInput = document.getElementById('preco');
const quantidadeInput = document.getElementById('quantidade');
const categoriaSelect = document.getElementById('categoria');
const submitBtn = document.getElementById('submit-btn');
const cancelBtn = document.getElementById('cancel-btn');
const formTitle = document.getElementById('form-title');
const searchInput = document.getElementById('search-input');
const searchBtn = document.getElementById('search-btn');
const showAllBtn = document.getElementById('show-all-btn');
const reportsBtn = document.getElementById('reports-btn');
const manageCategoriesBtn = document.getElementById('manage-categories-btn');
const produtosTbody = document.getElementById('produtos-tbody');
const loadingDiv = document.getElementById('loading');
const errorMessageDiv = document.getElementById('error-message');
const categoriesModal = document.getElementById('categories-modal');
const closeModal = document.querySelector('.close');
const categoriaForm = document.getElementById('categoria-form');
const categoriaIdInput = document.getElementById('categoria-id');
const categoriaNomeInput = document.getElementById('categoria-nome');
const categoriaDescricaoInput = document.getElementById('categoria-descricao');
const categorySubmitBtn = document.getElementById('category-submit-btn');
const categoryCancelBtn = document.getElementById('category-cancel-btn');
const categoryFormTitle = document.getElementById('category-form-title');
const categoriesTbody = document.getElementById('categories-tbody');
const categoriesLoadingDiv = document.getElementById('categories-loading');
const categoriesErrorMessageDiv = document.getElementById('categories-error-message');
const reportsModal = document.getElementById('reports-modal');
const stockLimitInput = document.getElementById('stock-limit');
const loadLowStockBtn = document.getElementById('load-low-stock-btn');
const loadStockValueBtn = document.getElementById('load-stock-value-btn');
const stockValueDisplay = document.getElementById('stock-value-display');
const reportsLoadingDiv = document.getElementById('reports-loading');
const reportsErrorMessageDiv = document.getElementById('reports-error-message');
const reportsTable = document.getElementById('reports-table');
const reportsTbody = document.getElementById('reports-tbody');
const paginationControls = document.getElementById('pagination-controls');
const prevPageBtn = document.getElementById('prev-page-btn');
const nextPageBtn = document.getElementById('next-page-btn');
const pageInfo = document.getElementById('page-info');

let editingProductId = null;
let editingCategoryId = null;
let currentPage = 0;
let totalPages = 0;

document.addEventListener('DOMContentLoaded', function() {
    carregarProdutos();
    carregarCategorias();
    setupEventListeners();
});
function setupEventListeners() {
    produtoForm.addEventListener('submit', handleFormSubmit);
    cancelBtn.addEventListener('click', cancelarEdicao);
    searchBtn.addEventListener('click', buscarProdutos);
    showAllBtn.addEventListener('click', carregarProdutos);
    reportsBtn.addEventListener('click', openReportsModal);
    manageCategoriesBtn.addEventListener('click', openCategoriesModal);

    const closeModals = document.querySelectorAll('.close');
    closeModals.forEach(close => {
        close.addEventListener('click', function() {
            const modal = close.closest('.modal');
            modal.style.display = 'none';
        });
    });

    categoriaForm.addEventListener('submit', handleCategoryFormSubmit);
    categoryCancelBtn.addEventListener('click', cancelarEdicaoCategoria);

    loadLowStockBtn.addEventListener('click', () => carregarEstoqueBaixo());
    loadStockValueBtn.addEventListener('click', carregarValorEstoque);
    prevPageBtn.addEventListener('click', () => carregarEstoqueBaixo(currentPage - 1));
    nextPageBtn.addEventListener('click', () => carregarEstoqueBaixo(currentPage + 1));

    window.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal')) {
            event.target.style.display = 'none';
        }
    });

    searchInput.addEventListener('keyup', function(e) {
        if (e.key === 'Enter') {
            buscarProdutos();
        }
    });
}

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

async function carregarProdutos() {
    try {
        const produtos = await apiRequest(API_BASE_URL);
        renderizarProdutos(produtos);
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
    }
}

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

function openReportsModal() {
    reportsModal.style.display = 'block';
    stockValueDisplay.innerHTML = '';
    reportsTable.style.display = 'none';
    paginationControls.style.display = 'none';
}

async function carregarEstoqueBaixo(page = 0) {
    try {
        showReportsLoading(true);
        hideReportsError();

        const quantidade = parseInt(stockLimitInput.value) || 10;
        const response = await fetch(`${API_BASE_URL}/low-stock?quantidade=${quantidade}&page=${page}&size=10`);

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        const data = await response.json();
        currentPage = data.number;
        totalPages = data.totalPages;

        renderizarRelatorios(data.content);
        atualizarPaginacao();

        reportsTable.style.display = 'table';
        paginationControls.style.display = 'block';
    } catch (error) {
        showReportsError(error.message);
        console.error('Erro ao carregar estoque baixo:', error);
    } finally {
        showReportsLoading(false);
    }
}

async function carregarValorEstoque() {
    try {
        showReportsLoading(true);
        hideReportsError();

        const response = await fetch(`${API_BASE_URL}/stock-value`);

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        const valor = await response.json();
        stockValueDisplay.innerHTML = `<h4>Valor Total: R$ ${valor.toFixed(2)}</h4>`;

        reportsTable.style.display = 'none';
        paginationControls.style.display = 'none';
    } catch (error) {
        showReportsError(error.message);
        console.error('Erro ao calcular valor do estoque:', error);
    } finally {
        showReportsLoading(false);
    }
}

function renderizarRelatorios(produtos) {
    reportsTbody.innerHTML = '';

    if (!produtos || produtos.length === 0) {
        reportsTbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center;">Nenhum produto encontrado</td>
            </tr>
        `;
        return;
    }

    produtos.forEach(produto => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${produto.id}</td>
            <td>${produto.nome}</td>
            <td>${produto.descricao || ''}</td>
            <td>R$ ${parseFloat(produto.preco).toFixed(2)}</td>
            <td>${produto.quantidade}</td>
            <td>${produto.categoria ? produto.categoria.nome : ''}</td>
        `;
        reportsTbody.appendChild(row);
    });
}

function atualizarPaginacao() {
    prevPageBtn.disabled = currentPage === 0;
    nextPageBtn.disabled = currentPage >= totalPages - 1;
    pageInfo.textContent = `Página ${currentPage + 1} de ${totalPages}`;
}

function showReportsLoading(show) {
    reportsLoadingDiv.style.display = show ? 'block' : 'none';
}

function hideReportsError() {
    reportsErrorMessageDiv.style.display = 'none';
}

function showReportsError(message) {
    reportsErrorMessageDiv.textContent = message;
    reportsErrorMessageDiv.style.display = 'block';
}

function renderizarProdutos(produtos) {
    produtosTbody.innerHTML = '';
    
    if (!produtos || produtos.length === 0) {
        produtosTbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 40px; color: #666;">
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
            <td>${produto.categoria ? produto.categoria.nome : '-'}</td>
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

async function handleFormSubmit(e) {
    e.preventDefault();
    
    const categoriaId = categoriaSelect.value;
    const produto = {
        nome: nomeInput.value.trim(),
        descricao: descricaoInput.value.trim(),
        preco: parseFloat(precoInput.value),
        quantidade: parseInt(quantidadeInput.value),
        categoria: categoriaId ? { id: parseInt(categoriaId) } : null
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

async function editarProduto(id) {
    try {
        const produto = await apiRequest(`${API_BASE_URL}/${id}`);
        
        produtoIdInput.value = produto.id;
        nomeInput.value = produto.nome;
        descricaoInput.value = produto.descricao || '';
        precoInput.value = produto.preco;
        quantidadeInput.value = produto.quantidade;
        categoriaSelect.value = produto.categoria ? produto.categoria.id : '';
        
        editingProductId = id;
        formTitle.textContent = 'Editar Produto';
        submitBtn.textContent = 'Atualizar Produto';
        cancelBtn.style.display = 'inline-block';
        
        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Erro ao carregar produto para edição:', error);
    }
}

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

function cancelarEdicao() {
    limparFormulario();
}

function limparFormulario() {
    produtoForm.reset();
    produtoIdInput.value = '';
    editingProductId = null;
    formTitle.textContent = 'Adicionar Produto';
    submitBtn.textContent = 'Adicionar Produto';
    cancelBtn.style.display = 'none';
}

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
    const existingMessage = document.querySelector('.success-message');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message';
    successDiv.textContent = message;
    
    const main = document.querySelector('main');
    main.insertBefore(successDiv, main.firstChild);
    
    setTimeout(() => {
        successDiv.remove();
    }, 3000);
}

async function carregarCategorias() {
    try {
        const categorias = await fetch(CATEGORIA_API_BASE_URL)
            .then(response => response.json());

        const categoriaSelect = document.getElementById('categoria');
        categoriaSelect.innerHTML = '<option value="">Selecione uma categoria</option>';

        categorias.forEach(categoria => {
            const option = document.createElement('option');
            option.value = categoria.id;
            option.textContent = categoria.nome;
            categoriaSelect.appendChild(option);
        });

        renderizarCategorias(categorias);
    } catch (error) {
        console.error('Erro ao carregar categorias:', error);
    }
}

function openCategoriesModal() {
    categoriesModal.style.display = 'block';
    carregarCategoriasModal();
}

function closeCategoriesModal() {
    categoriesModal.style.display = 'none';
    limparFormularioCategoria();
}

async function carregarCategoriasModal() {
    try {
        showCategoriesLoading(true);
        hideCategoriesError();

        const response = await fetch(CATEGORIA_API_BASE_URL);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Erro ${response.status}: ${errorText}`);
        }

        const categorias = await response.json();
        renderizarCategorias(categorias);
    } catch (error) {
        console.error('Erro ao carregar categorias:', error);
        showCategoriesError('Erro ao carregar categorias: ' + error.message);
    } finally {
        showCategoriesLoading(false);
    }
}

function renderizarCategorias(categorias) {
    categoriesTbody.innerHTML = '';

    if (!categorias || categorias.length === 0) {
        categoriesTbody.innerHTML = `
            <tr>
                <td colspan="4" style="text-align: center; padding: 40px; color: #666;">
                    Nenhuma categoria encontrada
                </td>
            </tr>
        `;
        return;
    }

    categorias.forEach(categoria => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${categoria.id}</td>
            <td>${categoria.nome}</td>
            <td>${categoria.descricao || '-'}</td>
            <td>
                <div class="action-buttons">
                    <button onclick="editarCategoria(${categoria.id})" class="success">Editar</button>
                    <button onclick="deletarCategoria(${categoria.id})" class="danger">Excluir</button>
                </div>
            </td>
        `;
        categoriesTbody.appendChild(row);
    });
}

async function handleCategoryFormSubmit(e) {
    e.preventDefault();

    const categoria = {
        nome: categoriaNomeInput.value.trim(),
        descricao: categoriaDescricaoInput.value.trim()
    };

    try {
        let response;
        if (editingCategoryId) {
            response = await fetch(`${CATEGORIA_API_BASE_URL}/${editingCategoryId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(categoria)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Erro ${response.status}: ${errorText}`);
            }

            showCategoriesSuccess('Categoria atualizada com sucesso!');
        } else {
            response = await fetch(CATEGORIA_API_BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(categoria)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Erro ${response.status}: ${errorText}`);
            }

            showCategoriesSuccess('Categoria criada com sucesso!');
        }

        limparFormularioCategoria();
        carregarCategoriasModal();
        carregarCategorias();
    } catch (error) {
        console.error('Erro detalhado:', error);
        showCategoriesError('Erro ao salvar categoria: ' + error.message);
    }
}

async function editarCategoria(id) {
    try {
        const response = await fetch(`${CATEGORIA_API_BASE_URL}/${id}`);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Erro ${response.status}: ${errorText}`);
        }

        const categoria = await response.json();

        categoriaIdInput.value = categoria.id;
        categoriaNomeInput.value = categoria.nome;
        categoriaDescricaoInput.value = categoria.descricao || '';

        editingCategoryId = id;
        categoryFormTitle.textContent = 'Editar Categoria';
        categorySubmitBtn.textContent = 'Atualizar Categoria';
        categoryCancelBtn.style.display = 'inline-block';
    } catch (error) {
        console.error('Erro ao carregar categoria:', error);
        showCategoriesError('Erro ao carregar categoria: ' + error.message);
    }
}

async function deletarCategoria(id) {
    if (!confirm('Tem certeza que deseja excluir esta categoria?')) {
        return;
    }

    try {
        const response = await fetch(`${CATEGORIA_API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            if (response.status === 409) {
                showCategoriesError('Não é possível excluir esta categoria pois ela possui produtos associados.');
            } else {
                const errorText = await response.text();
                showCategoriesError(`Erro ao deletar categoria: ${errorText}`);
            }
            return;
        }

        showCategoriesSuccess('Categoria excluída com sucesso!');
        carregarCategoriasModal();
        carregarCategorias();
    } catch (error) {
        console.error('Erro ao deletar categoria:', error);
        showCategoriesError('Erro ao deletar categoria: ' + error.message);
    }
}

function cancelarEdicaoCategoria() {
    limparFormularioCategoria();
}

function limparFormularioCategoria() {
    categoriaForm.reset();
    categoriaIdInput.value = '';
    editingCategoryId = null;
    categoryFormTitle.textContent = 'Adicionar Categoria';
    categorySubmitBtn.textContent = 'Adicionar Categoria';
    categoryCancelBtn.style.display = 'none';
}

function showCategoriesLoading(show) {
    categoriesLoadingDiv.style.display = show ? 'block' : 'none';
}

function showCategoriesError(message) {
    categoriesErrorMessageDiv.textContent = message;
    categoriesErrorMessageDiv.style.display = 'block';
    setTimeout(() => {
        hideCategoriesError();
    }, 5000);
}

function hideCategoriesError() {
    categoriesErrorMessageDiv.style.display = 'none';
}

function showCategoriesSuccess(message) {
    const existingMessage = document.querySelector('.modal-body .success-message');
    if (existingMessage) {
        existingMessage.remove();
    }

    const successDiv = document.createElement('div');
    successDiv.className = 'success-message';
    successDiv.textContent = message;

    const modalBody = document.querySelector('.modal-body');
    modalBody.insertBefore(successDiv, modalBody.firstChild);

    setTimeout(() => {
        successDiv.remove();
    }, 3000);
}

