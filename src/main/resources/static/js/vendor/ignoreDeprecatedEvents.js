;(function() {
  const orig = Element.prototype.addEventListener;
  Element.prototype.addEventListener = function(type, listener, options) {
    if (type === 'DOMNodeInserted') {
      return; // deprecated 이벤트 등록 무시!
    }
    return orig.call(this, type, listener, options);
  };
})();