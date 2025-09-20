// Toggle password visibility
function togglePassword(id, iconId) {
    const input = document.getElementById(id);
    const icon = document.getElementById(iconId);
    if (input.type === "password") {
        input.type = "text";
        icon.textContent = "🙈";
    } else {
        input.type = "password";
        icon.textContent = "👁️";
    }
}

// Role-based button colors
function updateButtonColor(roleSelectId, buttonId) {
    const role = document.getElementById(roleSelectId).value;
    const button = document.getElementById(buttonId);

    if (role === "citizen") {
        button.style.background = "linear-gradient(90deg, #4CAF50, #81C784)";
    } else if (role === "official") {
        button.style.background = "linear-gradient(90deg, #2196F3, #64B5F6)";
    } else if (role === "analyst") {
        button.style.background = "linear-gradient(90deg, #FF9800, #FFB74D)";  // New color for analyst
    } else {
        button.style.background = "linear-gradient(90deg, #38b000, #00b4d8)";
    }
}


// Handle form submit with loading state

async function handleSubmit(event, buttonId, errorId) {
    event.preventDefault();

    const button = document.getElementById(buttonId);
    const error = document.getElementById(errorId);

    error.style.display = "none";  // Hide previous errors
    button.textContent = "Loading...";
    button.disabled = true;

    // Get form data
    const form = event.target;
    const username = form.querySelector('input[type="text"]').value;
    const email = form.querySelector('input[type="email"]').value;
    const password = form.querySelector('input[type="password"]').value;
    const role = form.querySelector('select').value;

    // Prepare user data object
    const userData = {
        username: username,
        email: email,
        password: password,
        roles: [role]
    };

    try {
        // Send POST request to backend
        const response = await fetch('http://localhost:8080/users/addUser', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)  // convert object to JSON string
        });

        if (response.ok) {
            // Registration successful
            window.location.href = 'login.html';  // redirect to login page
        } else {
            // Backend returned error
            const message = await response.text();  // get error text from backend
            error.textContent = message || "Registration failed!";
            error.style.display = "block";
        }
    } catch (err) {
        // Network or server error
        error.textContent = "Server error!";
        error.style.display = "block";
    }

    button.textContent = buttonId === "loginBtn" ? "Login" : "Register";
    button.disabled = false;
}

async function handleLogin(event, buttonId, errorId) {
    event.preventDefault();
    const button = document.getElementById(buttonId);
    const error = document.getElementById(errorId);
    error.style.display = "none";
    button.textContent = "Loading...";
    button.disabled = true;

    const form = event.target;
    const username = form.querySelector('input[type="text"]').value;
    const password = form.querySelector('input[type="password"]').value;

    try {
        const response = await fetch('http://localhost:8080/users/login', {  // Changed endpoint to /login
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const user = await response.json(); // Expecting full user object with roles
            if (!user || !user.roles || user.roles.length === 0) {
                error.textContent = "No roles assigned!";
                error.style.display = "block";
            } else {
                // Role-based redirection
                if (user.roles.some(role => role.toUpperCase() === "OFFICIAL")) {
                    window.location.href = 'officialView.html';
                } else if (user.roles.some(role => role.toUpperCase() === "ANALYST")) {
                    window.location.href = 'analystView.html';
                } else if (user.roles.some(role => role.toUpperCase() === "USER")) {
                    window.location.href = 'userView.html';
                } else {
                    error.textContent = "Unknown role!";
                    error.style.display = "block";
                }

            }
        } else {
            error.textContent = "Invalid username or password";
            error.style.display = "block";
        }
    } catch (err) {
        console.error(err);
        error.textContent = "Server error!";
        error.style.display = "block";
    }

    button.textContent = "Login";
    button.disabled = false;

}
