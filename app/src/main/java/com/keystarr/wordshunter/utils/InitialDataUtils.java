package com.keystarr.wordshunter.utils;

import android.content.Context;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.models.local.Limiter;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Cyril on 11.07.2017.
 */

public final class InitialDataUtils {
    private static String[] defaultWords = {"лол", "кек", "хы",
            "вероятно", "наверное", "возможно", "типа", "короче", "ёпт",
            "сорян", "го", "фак", "блин", "пипец", "капец", "чё", "прив", "ваще", "мб", "хз"};
    private static String[] defaultGroups =
            {"Паразиты", "Неуверенность", "Связки",
                    "Заимствования", "Разочарование", "Сокращения"};

    private InitialDataUtils() {
    }

    public static List<WordsGroupToTrack> initializeWordsGroupsToTrackListFromDefaults(Context context) {
        List<WordsGroupToTrack> wordsGroupsToTrackList = new ArrayList<>();
        for (int i = 0; i < defaultGroups.length; i++) {
            WordsGroupToTrack wordsGroup = new WordsGroupToTrack(defaultGroups[i], wordsGroupsToTrackList.size() + 1);
            List<WordToTrack> wordsList = new ArrayList<>();
            for (int j = 3 * i; j < 3 * i + 3; j++) {
                wordsList.add(new WordToTrack(defaultWords[j], defaultGroups[i], true, wordsList.size()));
            }
            wordsGroup.setWordsToTrack(wordsList);
            wordsGroupsToTrackList.add(wordsGroup);
        }
        wordsGroupsToTrackList.get(5).getWordsToTrack().add(3,
                new WordToTrack(defaultWords[18], "Сокращения", true, 3));
        wordsGroupsToTrackList.get(5).getWordsToTrack().add(4,
                new WordToTrack(defaultWords[19], "Сокращения", true, 4));
        wordsGroupsToTrackList.add(0, new WordsGroupToTrack(context.getString(R.string.no_group), 0));
        return wordsGroupsToTrackList;
    }

    public static List<Limiter> initializeLimitersFromDefaults() {
        List<Limiter> limitersList = new ArrayList<>();
        limitersList.add(new Limiter(defaultWords[0], 1, true));
        limitersList.add(new Limiter(defaultWords[4], 1, true));
        limitersList.add(new Limiter(defaultWords[6], 1, true));
        limitersList.add(new Limiter(defaultWords[7], 1, true));
        return limitersList;
    }
}
