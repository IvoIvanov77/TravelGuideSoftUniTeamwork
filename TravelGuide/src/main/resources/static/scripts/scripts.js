$(function() {
    $('#messages').find('li').click(function() {
        $(this).fadeOut();
    });
    setTimeout(function() {
        $('#messages').find('li').fadeOut();
    }, 3000);
});
