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
    createMap();
    
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
    for (i = 0; i < parsedText.comments.length; i++) {
        const comment = parsedText.comments[i];
        const heading = ` (${comment.email}, posted at ${comment.date})`;
        const row = makeComment(comment.name + heading, comment.content, comment.isOwnComment, comment.commentId);
        table.appendChild(row);
    }
}

function makeComment(heading, content, isOwnComment, commentId) {
    const row = document.createElement("tr")

    var th = document.createElement("th");
    if (isOwnComment) {
        th = addDeleteButton(heading, commentId);
    } else {
        th.innerText = heading;
    }
    
    const td = document.createElement("td");
    td.innerText = content;

    row.appendChild(th);
    row.appendChild(td)

    return row;
}

function addDeleteButton(heading, commentId) {
    const th = document.createElement("th");
    th.innerText = heading;

    const template = document.getElementById("delete-template");
    const form = template.content.cloneNode(true);
    const id = form.querySelector("input");
    id.value = commentId;

    th.append(form);
    return th;
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
    table.innerHTML = "";
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

function createMap() {
    const snoopy = {
        lat: 25.4924, 
        lng: 56.3641
    };

    const map = new google.maps.Map(
        document.getElementById("map"),
        {
            center: snoopy,
            mapTypeId: 'satellite', 
            zoom: 18
        });

    const marker = new google.maps.Marker({
        position: snoopy,
        map: map
    });

    var infowindow = new google.maps.InfoWindow({
        content: "apparently this place is called snoopy" +
        " island because it looks like snoopy, but i "
        + "honestly don't see it"
    });
    infowindow.open(map, marker); 
}