var protocol = window.location.protocol
var hostname = window.location.hostname;
var port = window.location.port;

function createUser(){
    $(document).ready(function() {
        $('#addUserBtn').click(function() {
           $('#addUserModal').modal('show');
            });
        });

       $(document).ready(function() {
            $('#phone').inputmask('+375 (29) 999-99-99');
            $('#roles').one('focus', function() {
                $(this).select2({
                    width: '100%',
                    placeholder: $(this).data('placeholder'),
                    closeOnSelect: false,
                    allowClear: true,
                    dropdownParent: $('#addUserModal')
                });
            });
            $('#togglePassword').click(function() {
                        const passwordField = $('#password');
                        const type = passwordField.attr('type') === 'password' ? 'text' : 'password';
                        passwordField.attr('type', type);
                        $(this).find('i').toggleClass('fa-eye fa-eye-slash');
                    });
                    $('#saveUserBtn').click(function() {
                        const form = $('#addUserForm')[0];
                        if (!form.checkValidity()) {
                            form.reportValidity();
                            return;
                        }
                        const userData = {
                            login: $('#login').val(),
                            password: $('#password').val(),
                            lastName: $('#lastName').val(),  
                            firstName: $('#firstName').val(),
                            middleName: $('#middleName').val(),
                            email: $('#email').val(),
                            phone: $('#phone').val(), 
                            roles: $('#roles').val()
                        };
                        $.ajax({
                            url: protocol + '//'+ hostname + ':' + port +'/users/create',
                            method: 'POST',
                            contentType: 'application/json',
                            data: JSON.stringify(userData),
                            success: function(response) {
                                $('#addUserModal').modal('hide');
                                if (window.refreshUsersTable) {
                                    refreshUsersTable();
                                }
                            },
                            error: function(xhr) {
                                console.error('Ошибка:', xhr.responseText);
                            }
                        });
                    });
                });

}