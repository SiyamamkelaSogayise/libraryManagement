document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent default form submission

    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    // Example: Send login request to server using Axios (you can use fetch or other libraries)
    axios.post('/admin-login', {
        username: username,
        password: password
    })
    .then(function(response) {
        // Handle successful login response
        console.log('Login successful');
        window.location.href = '/admin_portal.html'; // Redirect to admin portal on success
    })
    .catch(function(error) {
        // Handle login error (e.g., invalid credentials)
        console.error('Login error:', error.response.data);
        alert('Login failed. Please check your credentials.');
    });
});
