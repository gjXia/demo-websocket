var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var onlineSize = document.querySelector('#onlineSize');

var username = null;

var websocket = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        // 判断当前浏览器是否支持WebSocket
        if ('WebSocket' in window) {
            websocket = new WebSocket("ws://localhost:8080/socket/" + username);

            // 连接发生错误的回调方法
            websocket.onerror = function () {
                connectingElement.textContent = '无法连接到WebSocket服务器。请刷新此页再试一次！';
                connectingElement.style.color = 'red';
            };

            //连接成功建立的回调方法
            websocket.onopen = function () {
                connectingElement.classList.add('hidden');
            }

            //连接关闭的回调方法
            websocket.onclose = function(){
                connectingElement.textContent = 'WebSocket服务器已关闭。请稍后再试！';
                connectingElement.style.color = 'red';
            }

            //接收到消息的回调方法
            websocket.onmessage = function (event) {
                onMessageReceived(event.data);
            }

            // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
            window.onbeforeunload = function () {
                websocket.close();
            }
        } else {
            alert('Not support websocket')
        }
    }
    event.preventDefault();
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        websocket.send(JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(data) {
    var message = JSON.parse(data);

    var messageElement = document.createElement('li');

    if (message.type === 'JOIN' || message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        onlineSize.innerHTML = message.onlineSize;
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function closeWebSocket() {
    chatPage.classList.add('hidden');
    websocket.close();
    usernamePage.classList.remove('hidden');
    messageArea.innerHTML = "";
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)