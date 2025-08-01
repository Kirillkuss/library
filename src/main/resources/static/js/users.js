var protocol = window.location.protocol
var hostname = window.location.hostname;
var port = window.location.port;


function users(){
   
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
                loadUsers(currentPage, pageSize);
                function loadUsers(page, size) {
                    $('#loadingIndicator').show();
                    $('#usersTableBody').html('<tr><td colspan="8" class="text-center">Загрузка данных...</td></tr>');
                    
                    $.ajax({
                        url: protocol + "//"+ hostname + ':' + port + '/users/lazy/${page}/${size}',
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
                                renderUsers(users, page, size);
                                renderPagination( page+1, page );
                            } else {
                                $('#usersTableBody').html('<tr><td colspan="8" class="text-center">Пользователи не найдены</td></tr>');
                            }
                        },
                        error: function(xhr, status, error) {
                            $('#loadingIndicator').hide();
                            $('#usersTableBody').html('<tr><td colspan="8" class="text-center text-danger">Ошибка загрузки данных: ' + error + '</td></tr>');
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
                    loadUsers(currentPage, pageSize);
                });
            }
    });

    /**
     * Преобразовение 
     * @param {*} users 
     * @param {*} page 
     * @param {*} size 
     */
    function renderUsers(users, page, size) {
        const tableBody = $('#usersTableBody');
        tableBody.empty();
                    
        users.forEach((user, index) => {
            const isBlocked = String(user.status).toLowerCase() === 'true' || user.status === true || user.status === 1;
            const statusIcon = isBlocked 
                ? '<i class="fas fa-lock text-danger" title="Заблокирован"></i>'
                : '<i class="fas fa-lock-open text-success" title="Активен"></i>';
            const rowNumber = (page - 1) * size + index + 1;
            const statusText = user.status === "true" ? "Заблок." : "Разблок.";
            const rolesText = user.roles ? user.roles.join(', ') : 'Нет ролей';
            const row = `
                            <tr>
                                <td>${rowNumber}</td>
                                <td>${statusIcon}</td>
                                <td>${user.login || 'Не указано'}</td>
                                <td>${user.fio || 'Не указано'}</td>
                                <td>${user.email || 'Не указано'}</td>
                                <td>${user.phone || 'Не указано'}</td>
                                <td>${rolesText}</td>
                                <td>
                                    <button class="btn btn-sm btn-primary edit-user" data-id="${user.id}">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="btn btn-sm btn-danger delete-user" data-id="${user.id}">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        `;
                tableBody.append(row);
        });
    };
};


function findUsersbyUI() {
    $(document.getElementById("searchBtnUsers")).on( "click",function(){
        var param = $('#searchInputUsers').val();
        if(  param.length  == 0 ){
            $('#errorToast').text( 'Значение поля поиск не может быть пустым' ).show();
            $('#liveToastBtn').click();
            users();
        }else{
            $.ajax({
                type: "GET",
                contentType: "application/json; charset=utf-8",
                url: protocol + "//"+ hostname + ":" + port + "/users/{param}",
                data:{ param: param} ,
                cache: false,
                success: function(response) {
                            $('#loadingIndicator').hide();
                            let users = [];
                            if (response && Array.isArray(response)) {
                                users = response;
                            } else if (Array.isArray(response)) {
                                users = response;
                            }
                            if (users.length > 0) {
                                renderUsers(users, 1, 10);
                                //renderPagination( 1+1, 3 );
                            } else {
                                $('#usersTableBody').html('<tr><td colspan="8" class="text-center">Пользователи не найдены</td></tr>');
                            }
                        },
                        error: function(error) {
                            $('#loadingIndicator').hide();
                            $('#usersTableBody').html('<tr><td colspan="8" class="text-center text-danger">Ошибка загрузки данных: ' + error + '</td></tr>');
                            console.error('Ошибка AJAX:', error);
                }
            });
        }

    });	
};

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
            };

            function setupPaginationHandlers() {
                $(document).off('click', '.page-link');
                $(document).on('click', '.page-link', function(e) {
                    e.preventDefault();
                    const page = $(this).data('page');
                    currentPage = parseInt(page);
                    loadUsers(currentPage, pageSize);
                });
            };
    /**
     * Преобразовение 
     * @param {*} users 
     * @param {*} page 
     * @param {*} size 
     */
    function renderUsers(users, page, size) {
        const tableBody = $('#usersTableBody');
        tableBody.empty();
                    
        users.forEach((user, index) => {
            const isBlocked = String(user.status).toLowerCase() === 'true' || user.status === true || user.status === 1;
            const statusIcon = isBlocked 
                ? '<i class="fas fa-lock text-danger" title="Заблокирован"></i>'
                : '<i class="fas fa-lock-open text-success" title="Активен"></i>';
            const rowNumber = (page - 1) * size + index + 1;
            const statusText = user.status === "true" ? "Заблок." : "Разблок.";
            const rolesText = user.roles ? user.roles.join(', ') : 'Нет ролей';
            const row = `
                            <tr>
                                <td>${rowNumber}</td>
                                <td>${statusIcon}</td>
                                <td>${user.login || 'Не указано'}</td>
                                <td>${user.fio || 'Не указано'}</td>
                                <td>${user.email || 'Не указано'}</td>
                                <td>${user.phone || 'Не указано'}</td>
                                <td>${rolesText}</td>
                                <td>
                                    <button class="btn btn-sm btn-primary edit-user" data-id="${user.id}">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="btn btn-sm btn-danger delete-user" data-id="${user.id}">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        `;
                tableBody.append(row);
        });
    };