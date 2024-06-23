document.addEventListener('DOMContentLoaded', function() {
    const sidebarLinks = document.querySelectorAll('.sidebar ul li a');
    const contentSections = document.querySelectorAll('.content-section');

    // Function to show/hide content sections based on sidebar link clicks
    function showContent(sectionId) {
        // Hide all content sections
        contentSections.forEach(section => {
            section.style.display = 'none';
        });

        // Show the selected content section
        const selectedSection = document.getElementById(sectionId);
        if (selectedSection) {
            selectedSection.style.display = 'block';
        }
    }

    // Initially show 'Add Book' section
    showContent('addBook');

    // Event listener for sidebar links
    sidebarLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const sectionId = link.getAttribute('href').slice(1); // Remove '#'
            showContent(sectionId);
        });
    });

    // Search functionality
    const searchBooksForm = document.getElementById('searchBooksForm');
    const searchTermInput = document.getElementById('searchTerm');
    const searchButton = document.getElementById('searchButton');
    const bookTableBody = document.getElementById('bookTableBody');

    searchButton.addEventListener('click', function() {
        performSearch();
    });

    searchBooksForm.addEventListener('submit', function(e) {
        e.preventDefault();
        performSearch();
    });

    function performSearch() {
        const searchTerm = searchTermInput.value.trim().toLowerCase();

        // Clear previous search results
        bookTableBody.innerHTML = '';

        // Simulated list of available books (replace with actual data from backend)
        const availableBooks = [
            { title: 'Book 1', author: 'Author A', publicationDate: '2023-01-01', category: 'Fiction' },
            { title: 'Book 2', author: 'Author B', publicationDate: '2022-05-15', category: 'Non-Fiction' },
            { title: 'Book 3', author: 'Author C', publicationDate: '2021-12-31', category: 'Biography' }
            // Add more books as needed
        ];

        // Filter books based on search term (title or author)
        const filteredBooks = availableBooks.filter(book =>
            book.title.toLowerCase().includes(searchTerm) ||
            book.author.toLowerCase().includes(searchTerm)
        );

        // Populate table with filtered books
        filteredBooks.forEach(book => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${book.publicationDate}</td>
                <td>${book.category}</td>
            `;
            bookTableBody.appendChild(row);
        });

        // If no results found
        if (filteredBooks.length === 0) {
            const noResultsRow = document.createElement('tr');
            noResultsRow.innerHTML = '<td colspan="4" style="text-align: center;">No results found</td>';
            bookTableBody.appendChild(noResultsRow);
        }
    }

    // Additional functionality for search icon (optional)
    const searchIcon = document.createElement('i');
    searchIcon.classList.add('fas', 'fa-search');
    searchButton.appendChild(searchIcon);

});

// Simulated backend response data (replace with actual API calls in your implementation)
const simulatedBackend = {
    getUserData: async function(userId) {
        // Simulating fetching user data from backend
        return {
            bookReturned: false, // Simulated book return status
            arrearsDays: 10,     // Simulated arrears days
            penaltiesPaid: false, // Simulated penalties payment status
            suspended: false // Simulated suspension status
        };
    },
    suspendUser: async function(userId) {
        // Simulating suspending user (could be an API call to Java backend)
        console.log(`Simulating suspension of user ${userId}`);
        return true; // Return true if successful, false otherwise
    },
    unsuspendUser: async function(userId) {
        // Simulating unsuspending user (could be an API call to Java backend)
        console.log(`Simulating unsuspension of user ${userId}`);
        return true; // Return true if successful, false otherwise
    }
};

// Function to determine if user can be suspended
async function canSuspendUser(userId) {
    try {
        const userData = await simulatedBackend.getUserData(userId);
        return !userData.bookReturned && userData.arrearsDays >= 7;
    } catch (error) {
        console.error('Error fetching user data:', error);
        return false;
    }
}

// Function to suspend a user
async function suspendUser(userId) {
    try {
        const canSuspend = await canSuspendUser(userId);
        if (canSuspend) {
            const success = await simulatedBackend.suspendUser(userId);
            if (success) {
                console.log('User suspended successfully');
                // Optionally update UI to reflect user's suspension status
            } else {
                console.error('Failed to suspend user');
            }
        } else {
            console.log('User cannot be suspended at this time');
        }
    } catch (error) {
        console.error('Error suspending user:', error);
    }
}

// Function to unsuspend a user
async function unsuspendUser(userId) {
    try {
        const userData = await simulatedBackend.getUserData(userId);
        if (userData.bookReturned && userData.penaltiesPaid) {
            const success = await simulatedBackend.unsuspendUser(userId);
            if (success) {
                console.log('User unsuspended successfully');
                // Optionally update UI to reflect user's suspension status
            } else {
                console.error('Failed to unsuspend user');
            }
        } else {
            console.log('User cannot be unsuspended at this time');
        }
    } catch (error) {
        console.error('Error unsuspending user:', error);
    }
}

// Function to unsuspend a user by ID entered in the input field
async function unsuspendUserById() {
    const userIdInput = document.getElementById('userId').value.trim();
    if (userIdInput) {
        unsuspendUser(userIdInput);
    } else {
        console.error('Please enter a valid User ID');
    }
}

// Event listeners for buttons
document.addEventListener('DOMContentLoaded', () => {
    const userId = 'user123'; // Replace with actual user ID

    suspendUserButtons = document.querySelectorAll('[id^=suspendButton]');
    suspendUserButtons.forEach(button => {
        button.addEventListener('click', () => {
            const userId = button.id.replace('suspendButton_', '');
            suspendUser(userId);
        });
    });

    unsuspendUserButtons = document.querySelectorAll('[id^=unsuspendButton]');
    unsuspendUserButtons.forEach(button => {
        button.addEventListener('click', () => {
            const userId = button.id.replace('unsuspendButton_', '');
            unsuspendUser(userId);
        });
    });
});

document.addEventListener('DOMContentLoaded', function() {
    // Embed configuration
    var embedConfiguration = {
        type: 'dashboard',
        id: '<YOUR_POWER_BI_DASHBOARD_ID>', // Replace with your Power BI dashboard ID
        embedUrl: '<YOUR_POWER_BI_EMBED_URL>', // Replace with your Power BI dashboard embed URL
        accessToken: '<YOUR_POWER_BI_EMBED_TOKEN>', // Replace with your Power BI embed token
        tokenType: models.TokenType.Embed,
        permissions: models.Permissions.Read,
        settings: {
            panes: {
                filters: {
                    visible: false // Hide filters pane if needed
                }
            }
        }
    };

    // Embed the Power BI dashboard
    var dashboardContainer = document.getElementById('dashboardContainer');
    var dashboard = powerbi.embed(dashboardContainer, embedConfiguration);

    // Handle errors
    dashboard.on('error', function(event) {
        console.error(event.detail);
    });
});

    
