// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function age() {
    const birthday = new Date(2001, 5, 1); // my birthday june 1 2001
    const now = new Date();

    const diff = now.getFullYear() - birthday.getFullYear();
    // this only works because my birthday is on the 1st of the month
    const age = (now.getMonth() >= birthday.getMonth()) ? diff : diff - 1
    document.getElementById("age").innerText = `${age} years old`;
}

// TODO: create a function that returns the date in MM/DD/YYY format

function getMessages() {
    age() // you can only have one function in the onload attribute
    getDropdownVal()
    setCurrentPage()
    login();
    createMap();
    
    console.log("Fetching text.");
    const responsePromise = fetch("/data?pag=0");
    responsePromise.then(handleResponse);
}

function handleResponse(response) {
    console.log("Handling the response.");
    const textPromise = response.text();
    textPromise.then(addTextToDom);
}

function addTextToDom(text) {
    console.log("Adding text to DOM: "+text);
    const table = document.getElementById("comment-section");
    const parsedText = JSON.parse(text);

    var i;
    for (i = 0; i < parsedText.length; i++) {
        const comment = parsedText[i];
        const heading = ` (${comment.email}, posted at ${comment.date})`;
        const row = makeComment(comment.name + heading, comment.content);
        table.appendChild(row);
    }
}

function makeComment(heading, content) {
    const row = document.createElement("tr")
    const th = document.createElement("th");
    const td = document.createElement("td");

    th.innerText = heading;
    td.innerText = content;

    row.appendChild(th);
    row.appendChild(td)

    return row;
}

function getDropdownVal() {
    const dropdown = document.getElementById("num-comments");
    dropdown.onchange = changeDropdownVal;
    if (sessionStorage["num-comments"]) {
        dropdown.value = sessionStorage["num-comments"];
    }   
}

function changeDropdownVal() {
    const dropdown = document.getElementById("num-comments");
    sessionStorage["num-comments"] = dropdown.value;
    this.form.submit();
}

function deleteMessages() {
    console.log("Fetching response.");
    const responsePromise = fetch("/delete-data", {method:"POST"});
    responsePromise.then(handleDelete);
}

function handleDelete(response) {
    console.log("Handling the delete response.");
    const deletePromise = response.text();
    deletePromise.then(deleteTextFromDom);
}

function deleteTextFromDom(text) {
    console.log("Deleting text from DOM: "+text);
    const table = document.getElementById("comment-section");

    var i;
    for (i = 0; i < table.children.length; i++) {
        const comment = table.children[i];
        comment.remove();
    }
}

function setCurrentPage() {
    const current = document.getElementById("current-page");
    if (sessionStorage["current-page"]) {
        current.innerText = sessionStorage["current-page"];
    } else {
        sessionStorage["current-page"] = 1;
    }
}

function changeButtonValUp() {
    const pagination = document.getElementById("pag");
    pagination.value = 1;
    sessionStorage["current-page"]++;
    setCurrentPage();
}

function changeButtonValDown() {
    const pagination = document.getElementById("pag");
    pagination.value = -1;
    if (sessionStorage["current-page"] > 1) {
        sessionStorage["current-page"]--;
    }
    setCurrentPage();
}

function login() {
    fetch("/home")
    .then(response => response.text())
    .then(text => {
        const commentSection = document.getElementById("add-comment");
        console.log(text)
        const parsedText = JSON.parse(text);
        const link = document.createElement("a");
        link.setAttribute("href", parsedText.url);
        if (parsedText.isLoggedIn) {
            commentSection.removeAttribute("hidden");
            document.getElementById("name-link").hidden = false;
            link.innerText = "Logout here."
        } else {
            commentSection.hidden = true;
            document.getElementById("name-link").hidden = true;
            link.innerText = "Login here to comment."
        }
        document.getElementById("comments").appendChild(link);
    })
}

function getUsername() {
    document.getElementById("name-link").hidden = true;
    document.getElementById("nickname").removeAttribute("hidden");
}

function submitUsername() {
    document.getElementById("nickname").hidden = true;
    document.getElementById("name-link").removeAttribute("hidden");
}

function createMap() {
  const map = new google.maps.Map(
      document.getElementById("map"),
      {center: {lat: 37.422, lng: -122.084}, zoom: 16});
}