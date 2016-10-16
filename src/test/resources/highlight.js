var left = arguments[0];
var top = arguments[1];
var width = arguments[2];
var height = arguments[3];

var el = document.createElement('div');
el.setAttribute('id', 'highlighter');
el.style.position = 'fixed';
el.style.left = left + 'px';
el.style.top = top + 'px';
el.style.width = width + 'px';
el.style.height = height + 'px';
el.style.border='3px solid red';
document.body.appendChild(el);
