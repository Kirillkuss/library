var protocol = window.location.protocol
var hostname = window.location.hostname;
var port = window.location.port;


function books(){
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
                loadBooks(currentPage, pageSize);

                function loadBooks(page, size) {
                    $('#loadingIndicator').show();
                    $('#booksTableBody').html('<tr><td colspan="8" class="text-center">Загрузка данных...</td></tr>');
                    
                    $.ajax({
                        url: protocol + "//"+ hostname + ':' + port + '/books/lazy/${page}/${size}',
                        type: 'GET',
                        data: {
                            page: page,
                            size: size,
                        },
                        success: function(response) {
                            $('#loadingIndicator').hide();
                            let users = [];
                            if (response && Array.isArray(response)) {
                                users = response;
                                totalPages = response.totalPages;
                                totalElements = response.totalElements;
                            } else if (Array.isArray(response)) {
                                users = response;
                                totalPages = Math.ceil(users.length / size);
                                totalElements = users.length;
                            }
                            if (users.length > 0) {
                                renderBooks(users, page, size);
                                renderPagination( page+1, page );
                            } else {
                                $('#booksTableBody').html('<tr><td colspan="8" class="text-center">Книги не найдены</td></tr>');
                            }
                        },
                        error: function(xhr, status, error) {
                            $('#loadingIndicator').hide();
                            $('#booksTableBody').html('<tr><td colspan="8" class="text-center text-danger">Ошибка загрузки данных: ' + error + '</td></tr>');
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
                    loadBooks(currentPage, pageSize);
                });
            }
    });

    /**
     * Преобразовение 
     * @param {*} books 
     * @param {*} page 
     * @param {*} size 
     */
    function renderBooks(books, page, size) {
        const icon = '<i class="fas fa-book" title="Книга"></i>';
        const tableBody = $('#booksTableBody');
        tableBody.empty();
                    
        books.forEach((book, index) => {
            const rowNumber = (page - 1) * size + index + 1;
            const row = `
                            <tr>
                                <td>${icon} ${rowNumber}</td>
                                <td>${book.luDate || 'Не указано'}</td>
                                <td>${book.nameBook || 'Не указано'}</td>
                                <td>${book.descriptionBook || 'Не указано'}</td>
                                <td>${book.bookNumber || 'Не указано'}</td>
                                <td>${book.pages || 'Не указано'}</td>
                                <td>
                                    <button class="btn btn-sm btn-primary edit-book" data-id="${book.id}" type="button">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="btn btn-sm btn-danger delete-book" data-id="${book.id}" type="button">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        `;
                tableBody.append(row);
        });
    };
};

