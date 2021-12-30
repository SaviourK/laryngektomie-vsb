package cz.laryngektomie.helper;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ForumHelperTest {


    @Test
    public void getListOfPageNumbers() {
        int totalPages = 30;
        int currentPage = 8;

        List<Integer> expectedIntegersList = Arrays.asList(1, 6, 7, 8, 9, 10, 30);

        List<Integer> listOfPageNumbers = ForumHelper.getListOfPageNumbers(totalPages, currentPage);
        assertEquals(expectedIntegersList, listOfPageNumbers);
    }

    @Test
    public void getCreatedTimeString() {
        String result = ForumHelper.getCreatedTimeString(
                LocalDateTime.of(2013, Month.FEBRUARY, 1, 14, 15, 0)
        );
        assertEquals("14:15 01.02.2013", result);
    }


    @Test
    public void getDescription() {
        String text = "<b>V posledním listopadovém týdnu vyšlo druhé číslo Zpravodaje a měli byste ho již mít doma.</b></span><br></p><p style=\"\">z obsahu vyjímáme:</p><ul><li>na co nebyl v ordinaci čas:<br></li></ul><ul><li>odpovědi na otázky stran výživy</li></ul><ul><li>výsledky";
        String expected = "V posledním listopadovém týdnu vyšlo druhé číslo Z...";
        assertEquals(expected, ForumHelper.getDescription(text, 50));
    }


    @Test
    public void makeFriendlyUrl() {
        String friendlyUrl = ForumHelper.makeFriendlyUrl("Dne 9. 1. 2020 proběhlo povánoční tříkrálové posezení.");
        String expected = "dne-9-1-2020-probehlo-povanocni-trikralove-posezeni-";
        assertEquals(expected, friendlyUrl);
    }
}