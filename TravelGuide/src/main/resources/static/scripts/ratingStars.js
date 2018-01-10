$(()=>{
    let spanStars = $('div span.stars');
    $(spanStars).each(function () {
        // Get the value
        let val = parseFloat($(this).attr("value"));
        // Make sure that the value is in 0 - 5 range, multiply to get width
        let size = Math.max(0, (Math.min(5, val))) * 19;
        // Create stars holder
        let $span = $('<span />').width(size);
        // Replace the numerical value with stars
        $(this).html($span);
    });
});