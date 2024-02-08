window.onload = function() {
    loadData();

    const refreshButton = document.getElementById("refreshButton");
    refreshButton.addEventListener("click", loadData);
};

function loadData() {
    fetch("/pools")
        .then(response => response.json())
        .then(pools => {
            const tableBody = document.getElementById('poolsTable').getElementsByTagName('tbody')[0];

            // Clear existing table data
            tableBody.innerHTML = "";

            pools.forEach(pool => {
                let row = tableBody.insertRow();
                row.insertCell(0).innerText = pool.id;
                row.insertCell(1).innerText = pool.path;
                row.insertCell(2).innerText = pool.date || "N/A";
                row.insertCell(3).innerText = pool.active ? "Yes" : "No";
                let filesCell = row.insertCell(4);
                filesCell.innerText = pool.files.length;

                let viewFilesButton = document.createElement('button');
                viewFilesButton.innerText = 'View Files';
                viewFilesButton.addEventListener('click', function() {
                    window.location.href = `/view_files?poolId=${pool.id}`;
                });
                let actionCell = row.insertCell(5);
                actionCell.appendChild(viewFilesButton);
            });
        })
        .catch(error => console.error('Error:', error));
}