var protocol = window.location.protocol;
var hostname = window.location.hostname;
var port = window.location.port;

function cards() {
    let totalPages = 12;
    let currentPage = 1;
    const pageSize = 10;
    let totalElements = 0;

    $(document).ready(function() {
        $('.nav-link').click(function() {
            $('.nav-link').removeClass('active');
            $(this).addClass('active');
            const tabTitle = $(this).text().trim();
            $('.page-header h2').html('<i class="fas ' + $(this).find('i').attr('class').split(' ')[1] + ' me-2"></i> ' + tabTitle);
        });
        
        $('#sidebarToggle').click(function() {
            $('.sidebar').toggleClass('active');
        });

        loadCards(currentPage, pageSize);
    });

    function loadCards(page, size) {
        $('#loadingIndicator').show();
        $('#cardsTableBody').html('<tr><td colspan="8" class="text-center">Загрузка данных...</td></tr>');
        
        $.ajax({
            url: `${protocol}//${hostname}:${port}/cards/lazy/${page}/${size}`,
            type: 'GET',
            data: {
                page: page,
                size: size,
            },
            success: function(response) {
                $('#loadingIndicator').hide();
                let cards = [];
                if (response && Array.isArray(response)) {
                    cards = response;
                    totalPages = response.totalPages;
                    totalElements = response.totalElements;
                } else if (Array.isArray(response)) {
                    cards = response;
                    totalPages = Math.ceil(cards.length / size);
                    totalElements = cards.length;
                }
                if (cards.length > 0) {
                    renderCards(cards);
                    renderPagination(totalPages, currentPage);
                } else {
                    $('#cardsTableBody').html('<tr><td colspan="8" class="text-center">Карты не найдены</td></tr>');
                }
            },
            error: function(xhr, status, error) {
                $('#loadingIndicator').hide();
                $('#cardsTableBody').html('<tr><td colspan="8" class="text-center text-danger">Ошибка загрузки данных: ' + error + '</td></tr>');
                console.error('Ошибка AJAX:', error);
            }
        });
    }

    function renderPagination(totalPages, currentPage) {
        const pagination = $('#pagination');
        pagination.empty();
        pagination.append(`
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `);
        if (currentPage > 3) {
            pagination.append(`
                <li class="page-item">
                    <a class="page-link" href="#" data-page="1">1</a>
                </li>
                <li class="page-item disabled">
                    <span class="page-link">...</span>
                </li>
            `);
        }
        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, currentPage + 2);
        for (let i = startPage; i <= endPage; i++) {
            pagination.append(`
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }
        if (currentPage < totalPages - 2) {
            pagination.append(`
                <li class="page-item disabled">
                    <span class="page-link">...</span>
                </li>
                <li class="page-item">
                    <a class="page-link" href="#" data-page="${totalPages}">${totalPages}</a>
                </li>
            `);
        }
        pagination.append(`
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">
                    <i class="fas fa-chevron-right"></i>
                </a>
            </li>
        `);
        setupPaginationHandlers();
    }

    function setupPaginationHandlers() {
        $(document).off('click', '.page-link');
        $(document).on('click', '.page-link', function(e) {
            e.preventDefault();
            const page = $(this).data('page');
            currentPage = parseInt(page);
            loadCards(currentPage, pageSize);
        });
    }

    function renderCards(cards) {
        const tableBody = $('#cardsTableBody');
        tableBody.empty();
        
        cards.forEach((card) => {
        const isBlocked = String(card.isopen).toLowerCase() === 'true' || card.isopen === true || card.isopen === 1;
        const statusIcon = isBlocked 
            ? '<i class="fas fa-lock text-danger" title="Заблокирован"></i>'
            : '<i class="fas fa-lock-open text-success" title="Активен"></i>';

            const row = `
                <tr>
                    <td>${statusIcon}</td>
                    <td>${card.identifaication || 'Не указано'}</td>
                    <td>${card.user || 'Не указано'}</td>
                    <td>${card.login || 'Не указано'}</td>
                    <td>${card.phone || 'Не указано'}</td>
                    <td>${card.email || 'Не указано'}</td>
                    <td>${card.createDate || 'Не указано'}</td>
                    <td>${card.finishDate !== null ? card.finishDate : ''}</td>
                    <td>
                        <button class="btn btn-sm btn-primary edit-card" data-id="${card.identifaication}">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger delete-card" data-id="${card.identifaication}">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
            tableBody.append(row);
        });
    }
}