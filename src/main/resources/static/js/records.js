var protocol = window.location.protocol
var hostname = window.location.hostname;
var port = window.location.port;


function records(){
   
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
                loadRecords(currentPage, pageSize);

                function loadRecords(page, size) {
                    $('#loadingIndicator').show();
                    $('#recordsTableBody').html('<tr><td colspan="8" class="text-center">Загрузка данных...</td></tr>');
                    
                    $.ajax({
                        url: protocol + "//"+ hostname + ':' + port + '/library/records/lazy/${page}/${size}',
                        type: 'GET',
                        data: {
                            page: page,
                            size: size,
                        },
                        success: function(response) {
                            $('#loadingIndicator').hide();
                            let records = [];
                            if (response && Array.isArray(response)) {
                                records = response;
                                totalPages = response.totalPages;
                                totalElements = response.totalElements;
                            } else if (Array.isArray(response)) {
                                records = response;
                                totalPages = Math.ceil(records.length / size);
                                totalElements = users.length;
                            }
                            if (records.length > 0) {
                                renderRecords(records, page, size);
                                renderPagination( page+1, page );
                            } else {
                                $('#recordsTableBody').html('<tr><td colspan="8" class="text-center">Выдачи не найдены</td></tr>');
                            }
                        },
                        error: function(xhr, status, error) {
                            $('#loadingIndicator').hide();
                            $('#recordsTableBody').html('<tr><td colspan="8" class="text-center text-danger">Ошибка загрузки данных: ' + error + '</td></tr>');
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
                    loadRecords(currentPage, pageSize);
                });
            }
    });

    /**
     * Преобразовение 
     * @param {*} records 
     * @param {*} page 
     * @param {*} size 
     */
    function renderRecords(records, page, size) {
        const icon = '<i class="fas fa-exchange-alt" title="Записи"></i>';
        const tableBody = $('#recordsTableBody');
        tableBody.empty();
                    
        records.forEach((record, index) => {
            const rowNumber = (page - 1) * size + index + 1;
            const row = `
                            <tr>
                                <td>${icon} ${rowNumber}</td>
                                <td>${record.user || 'Не указано'}</td>
                                <td>${record.createDate || 'Не указано'}</td>
                                <td>${record.finishDate || 'Не указано'}</td>
                                <td>${record.book.nameBook || 'Не указано'}</td>
                                <td>${record.book.bookNumber || 'Не указано'}</td>
                            </tr>
                        `;
                tableBody.append(row);
        });
    };
};

