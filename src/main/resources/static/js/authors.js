var protocol = window.location.protocol
var hostname = window.location.hostname;
var port = window.location.port;


function authors(){
    $(document).ready(function(){
                $('.nav-link').click(function(){
                    $('.nav-link').removeClass('active');
                    $(this).addClass('active');
                    const tabTitle = $(this).text().trim();
                    $('.page-header h2').html('<i class="fas ' + $(this).find('i').attr('class').split(' ')[1] + ' me-2"></i> ' + tabTitle);
                });
                
                $('#sidebarToggle').click(function(){
                    $('.sidebar').toggleClass('active');
                });
                let totalPages = 12;
                let currentPage = 1;
                const pageSize = 10;
                let totalElements = 0;
                loadAuthors(currentPage, pageSize);

                function loadAuthors(page, size) {
                    $('#loadingIndicator').show();
                    $('#authorsTableBody').html('<tr><td colspan="8" class="text-center">Загрузка данных...</td></tr>');
                    
                    $.ajax({
                        url: protocol + "//"+ hostname + ':' + port + '/authors/lazy/${page}/${size}',
                        type: 'GET',
                        data: {
                            page: page,
                            size: size,
                        },
                        success: function(response) {
                            $('#loadingIndicator').hide();
                            let authors = [];
                            if (response && Array.isArray(response)) {
                                authors = response;
                                totalPages = response.totalPages;
                                totalElements = response.totalElements;
                            } else if (Array.isArray(response)) {
                                authors = response;
                                totalPages = Math.ceil(users.length / size);
                                totalElements = users.length;
                            }
                            if (authors.length > 0) {
                                renderAuthors(authors, page, size);
                                renderPagination( page+1, page );
                            } else {
                                $('#authorsTableBody').html('<tr><td colspan="8" class="text-center">Авторы не найдены</td></tr>');
                            }
                        },
                        error: function(xhr, status, error) {
                            $('#loadingIndicator').hide();
                            $('#authorsTableBody').html('<tr><td colspan="8" class="text-center text-danger">Ошибка загрузки данных: ' + error + '</td></tr>');
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
                    loadAuthors(currentPage, pageSize);
                });
            }
    });

    /**
     * Преобразовение 
     * @param {*} authors 
     * @param {*} page 
     * @param {*} size 
     */
    function renderAuthors(authors, page, size) {
        const tableBody = $('#authorsTableBody');
        tableBody.empty();
                    
        authors.forEach((author, index) => {
            const rowNumber = (page - 1) * size + index + 1;
            const row = `
                            <tr>
                                <td>${rowNumber}</td>
                                <td>${author.luDate || 'Не указано'}</td>
                                <td>${author.firstName + ' ' + author.secondName + ' ' + author.middleName || 'Не указано'}</td>
                                <td>${author.country || 'Не указано'}</td>
                                <td>
                                    <button class="btn btn-sm btn-primary edit-author" data-id="${author.id}">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="btn btn-sm btn-danger delete-author" data-id="${author.id}">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        `;
                tableBody.append(row);
        });
    };
};

