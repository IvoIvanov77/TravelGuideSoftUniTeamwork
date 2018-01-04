$(()=>{
    let spanStars = $('span.stars');
    $(spanStars).each(function () {
        // Get the value
        let val = parseFloat($(spanStars).attr("value"));
        // Make sure that the value is in 0 - 5 range, multiply to get width
        let size = Math.max(0, (Math.min(5, val))) * 16;
        // Create stars holder
        let $span = $('<span />').width(size);
        // Replace the numerical value with stars
        $(spanStars).html($span);
    });
});

