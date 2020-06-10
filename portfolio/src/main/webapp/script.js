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
    getDropdownVal();
    setCurrentPage();
    login();
    
    console.log("Fetching text.");
    const url = "?comments-to-show="+sessionStorage["num-comments"]+"&page-number="+sessionStorage["current-page"]
    const responsePromise = fetch("/data"+url);
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
    table.innerHTML = ""; // clears all the content of the table
    const parsedText = JSON.parse(text); // this is the object with properties numComments and comments
    sessionStorage["size"] = parsedText.numComments;

    var i;
    // const currPage = sessionStorage["current-page"];
    // const startComment = (currPage - 1) * sessionStorage["num-comments"];
    // const commentsToShow = currPage * sessionStorage["num-comments"];
    // for (i = startComment; i < Math.min(sessionStorage["size"], commentsToShow); i++) {

    for (i = 0; i < parsedText.comments.length; i++) {
        const comment = parsedText.comments[i];
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
    const defaultComments = 10; // move to top later
    sessionStorage["num-comments"] = sessionStorage["num-comments"] || defaultComments;
    dropdown.value = sessionStorage["num-comments"];
}

function changeDropdownVal() {
    const dropdown = document.getElementById("num-comments");
    sessionStorage["num-comments"] = dropdown.value;
    sessionStorage["current-page"] = 1; // go back to the first page of comments
    document.getElementById("dropdown").submit();
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
    const defaultPage = 1;
    sessionStorage["current-page"] = sessionStorage["current-page"] || defaultPage;
    current.innerText = sessionStorage["current-page"];
}


function changeButtonValUp() {
    const limit = Math.ceil(sessionStorage["size"] / sessionStorage["num-comments"]);
    if (sessionStorage["current-page"] < limit) {
        sessionStorage["current-page"]++;
        getMessages();
    }
}

function changeButtonValDown() {
    if (sessionStorage["current-page"] > 1) {
        sessionStorage["current-page"]--;
        getMessages();
    }
}

function login() {
    fetch("/home")
    .then(response => response.text())
    .then(text => {
        const commentSection = document.getElementById("add-comment");
        const parsedText = JSON.parse(text);
        const link = document.getElementById("login-link");
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