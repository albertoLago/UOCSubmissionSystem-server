const form = document.getElementById("poolForm");
const messageElement = document.getElementById("message");

form.addEventListener("submit", (e) => {
    e.preventDefault();

    const poolIDInput = document.getElementById("poolID");
    const poolID = poolIDInput.value;

    const fileInput = document.getElementById("zipFile");
    const file = fileInput.files[0];

    const dateInput = document.getElementById("selectedDate");
    const date = dateInput.value;

    const formData = new FormData();
    formData.append("file", file);
    formData.append("date", date);

    fetch(`/create_pool/${poolID}`, {
        method: "POST",
        body: formData,
    })
        .then((response) => {
            if (response.ok) {
                console.log("File sent to the backend.");
                messageElement.textContent = "Success.";
                messageElement.classList.remove('error');
                messageElement.classList.add('success');
                messageElement.style.display = "block";
            } else {
                console.error("Error while sending file to the backend.");
                messageElement.textContent = "Error while sending the file.";
                messageElement.classList.remove('success');
                messageElement.classList.add('error');
                messageElement.style.display = "block";
            }
            setTimeout(() => {
                messageElement.style.display = "none";
            }, 5000);
        })
        .catch((error) => {
            console.error("Network error:", error);
            messageElement.textContent = "Network error. Please try again.";
            messageElement.classList.remove('success');
            messageElement.classList.add('error');
            messageElement.style.display = "block";

            setTimeout(() => {
                messageElement.style.display = "none";
            }, 5000);
        });
});
