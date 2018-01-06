$(()=>{
    $('.rating')
        .addClass('starRating')
        .on('mouseenter', 'label', function(){
                DisplayRating($(this)); // show the ratings on label hover
            }
        )
        .on('mouseleave', function() {
                // when we leave the rating div, figure out which one is selected and show the correct rating level
                let $this = $(this),
                    $selectedRating = $this.find('input:checked');
                if ($selectedRating.length === 1) {
                    DisplayRating($selectedRating); // a rating has been selected, show the stars
                } else {
                    $this.find('label').removeClass('on'); // nothing clicked, remove the stars
                }
            }
        );

    let DisplayRating = function($el){
        // for the passed in element, add the 'on' class to this and all prev labels
        // and remove the 'on' class from all next labels. This stops the flicker of removing then adding back
        $el.addClass('on');
        $el.parent('label').addClass('on');
        $el.closest('span').prevAll().find('label').addClass('on');
        $el.closest('span').nextAll().find('label').removeClass('on');
    };
});
