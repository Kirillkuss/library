        $(document).ready(function() {
            $(".error-info").hide().delay(300).fadeIn(700);
            $(".suggestion").hide().delay(600).fadeIn(700);
            $(".home-button").hide().delay(900).fadeIn(700);
            $(".home-button").on('mouseenter', function() {
                $(this).css('transform', 'translateY(-3px)');
            }).on('mouseleave', function() {
                $(this).css('transform', 'translateY(0)');
            });
        });