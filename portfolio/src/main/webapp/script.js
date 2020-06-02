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
    console.log("Fetching text.");
    const responsePromise = fetch("/data");
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
    const parsedText = JSON.parse(text); // text is a string and not an array

    var i; 
    for (i = 0; i < parsedText.length; i++) {
        const comment = parsedText[i];
        const row = makeComment(comment.name, comment.content);
        table.appendChild(row);
    }
}

function makeComment(name, comment) {
    const row = document.createElement("tr")
    const heading = document.createElement("th");
    const data = document.createElement("td");

    heading.innerText = name;
    data.innerText = comment;

    row.appendChild(heading);
    row.appendChild(data)

    return row;
}