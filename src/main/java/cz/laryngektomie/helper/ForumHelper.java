package cz.laryngektomie.helper;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ForumHelper {

    public static final int itemsOnPage = 5;

    public static final int TITLE_IN_URL_MAX_LENGTH = 70;

    public static final Pattern DIACRITICS_AND_FRIENDS
            = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");


    public static List<Integer> getListOfPageNumbers(int totalPages, int currentPage){
        List<Integer> pageNumbers;


        int start = -1;
        int end = -1;

        if (totalPages <= 6) {
            start = 1;
            end  = totalPages;
        } else {
            start = Math.max(1, (currentPage - 2));
            end   = Math.min(totalPages, (currentPage + 2));

            if (start == 1) {
                end = 6;
            } else if (end == totalPages) {
                start = (totalPages - 5);
            }
        }

        pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        if(start != 1) {
            pageNumbers.add(0, 1);
        }

        if(end != totalPages){
            pageNumbers.add(totalPages);
        }

        return pageNumbers;


    }

    public static String getCreatedTimeString(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
    }



    public static String getDescription(String text, int length) {
        String description = text.replaceAll("\\<[^>]*>","");
        if (description.length() > length) {
            description = description.substring(0, length).concat("...");
        }
        return description;
    }


    //set dimension of the image
    private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }



    public static String makeFriendlyUrl(String name){
        String friendlyUrl = "";
        String titleInUrl = StringUtils.stripAccents(name);

        titleInUrl = titleInUrl.replaceAll("[\\-| |\\.]+", "-").toLowerCase();

        if(titleInUrl.length() > TITLE_IN_URL_MAX_LENGTH){
            friendlyUrl = (titleInUrl.substring(0, TITLE_IN_URL_MAX_LENGTH));
        } else {
            friendlyUrl = titleInUrl;
        }

        return friendlyUrl;

    }

    private static String stripDiacritics(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
        return str;
    }



}
