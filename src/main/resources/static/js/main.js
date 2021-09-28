/*Show/Hide*/
$(document).ready(function () {
    $(".toggleBtnAdvantages").click(function () {
        $(".toggleBtnAdvantages").toggleClass("active");
        $(".toggleBtnDisadvantages").removeClass("active");
        $(".toggleAdvantages").slideToggle("slow");
        $(".toggleDisadvantages").hide();
    });

    $(".toggleBtnDisadvantages").click(function () {
        $(".toggleBtnDisadvantages").toggleClass("active");
        $(".toggleBtnAdvantages").removeClass("active");
        $(".toggleDisadvantages").slideToggle("slow");
        $(".toggleAdvantages").hide();
    });

    //char counter for textarea
    $("#text").keyup(function () {
        $("#count").text("Zbýva: " + (1000 - $(this).val().length) + " znaků.");
    });
});

/*Sticky navbar onScroll*/
$(function () {
    //caches a jQuery object containing the header element
    var header = $(".navbar");
    $(window).scroll(function () {
        var scroll = $(window).scrollTop();

        if (scroll <= 300) {
            header.removeClass("navbar-scroll fixed-top");
        } else {
            header.addClass("navbar-scroll fixed-top");
        }
    });
});

/*Cookies popup*/
window.addEventListener("load", function () {
    window.cookieconsent.initialise({
        "palette": {
            "popup": {
                "background": "#577f398f",
                "text": "#ffffff"
            },
            "button": {
                "background": "#3f6844",
                "text": "#ffffff"
            }
        },
        "content": {
            "message": "Tento web používá k poskytování služeb a analýze návštěvnosti soubory cookie. Používáním tohoto webu souhlasíte s podmínkami používání souborů cookie.",
            "dismiss": "Souhlasím",
            "link": "Více informací",
            "href": "https://policies.google.com/technologies/cookies?hl=cs"
        }
    })
});


/*Komunikace START*/
//elementy ktere chci
var elements = Array.from(document.getElementsByClassName("rounded-pill"));

//obrazkove elementy
var elementsImg = Array.from(document.getElementsByClassName("w-75"));

var mql = window.matchMedia("(max-width: 430px)");

// funkce, která se zavolá při změně
mql.addListener(zmenaMedia);

// může se zavolat i po načtení stránky
zmenaMedia(mql);

// samotná funkce
function zmenaMedia(mql) {
    if (mql.matches) {

        elements.forEach(removeClass);
        elementsImg.forEach(changeClassTo100);
    }else {
        elements.forEach(addClass);
        elementsImg.forEach(changeClassTo75);
    }
}

function removeClass(item) {
    item.className = "";
}


function addClass(item) {
    item.className = "advantages p-5 rounded-pill";
}

function changeClassTo100(item) {
    item.className = "w-100 p-3";
}

function changeClassTo75(item) {
    item.className = "w-75 p-3";
}

/*Komunikace END*/


