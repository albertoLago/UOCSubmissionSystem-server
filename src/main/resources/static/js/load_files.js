// This function will get the poolId from the URL, update the header text,
// and get the pool data

function loadFiles() {
    const urlParams = new URLSearchParams(window.location.search);
    const poolId = urlParams.get('poolId');

    // Update the header text
    document.getElementById('poolId').innerText = poolId;

    // Gets the pool data
    fetch(`/pools/view_files?poolId=${poolId}`)
        .then(response => response.json())
        .then(pool => {
            // Clears the table
            const fileList = document.getElementById('fileList');
            fileList.innerHTML = '';

            // Adds each file to the table
            pool.files.forEach(file => {
                const row = fileList.insertRow();
                const cell = row.insertCell();
                cell.innerText = file;

                // Adds a click event to the row
                row.addEventListener('click', function() {
                    downloadFile(poolId, file);
                });
            });
        })
        .catch(error => console.error('Error:', error));
}

function downloadFile(poolId, filename) {
    window.location.href = `/pools/download?poolId=${poolId}&filename=${filename}`; // Por ejemplo
}
window.onload = loadFiles;

document.getElementById('downloadAllButton').addEventListener('click', function() {
    downloadAllFiles();
});

function downloadAllFiles() {
    const urlParams = new URLSearchParams(window.location.search);
    const poolId = urlParams.get('poolId');
    window.location.href = `/pools/downloadAll?poolId=${poolId}`;
}