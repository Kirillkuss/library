const baseUrl = `${window.location.protocol}//${window.location.host}`;

async function removeSessionAndExit() {
    try {
        const removeResponse = await fetch( `${baseUrl}/library/users/sessions/remove`, {
            method: 'DELETE',
            credentials: 'include'
        });
        if (!removeResponse.ok) {
            throw new Error('Ошибка при удалении сессии');
        }
        await exit();
    } catch (error) {
        console.error('Ошибка:', error);
        await exit();
    }
}

async function exit() {
    try {
        const response = await fetch('/library/logout', {
            method: 'POST',
            credentials: 'include'
        });
        if (response.ok) {
            window.location.href = "/library/login"; 
        } else {
            throw new Error(response.statusText);
        }
    } catch (error) {
        console.error('Ошибка при выходе:', error);
        window.location.href = "/library/login";
    }
}
