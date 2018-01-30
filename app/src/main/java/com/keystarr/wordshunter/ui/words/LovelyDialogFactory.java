package com.keystarr.wordshunter.ui.words;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.models.events.GroupAddedEvent;
import com.keystarr.wordshunter.models.events.LimiterAddedEvent;
import com.keystarr.wordshunter.models.events.LimiterChangedEvent;
import com.keystarr.wordshunter.models.events.LimiterDeletedEvent;
import com.keystarr.wordshunter.models.events.WordAddedEvent;
import com.keystarr.wordshunter.models.local.Limiter;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.squareup.otto.Bus;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.List;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

/**
 * Created by Cyril on 30.08.2017.
 */

public final class LovelyDialogFactory {

    static final int LIST_WORD_POSITION = 0;

    private static final String WORD_INPUT_REGEX = "[^\\s]{1,20}";
    private static final String GROUP_INPUT_REGEX = ".{1,20}";
    private static final String AGE_REGEX = "[0-9]{1,3}";

    private LovelyDialogFactory() {
    }

    static LovelyChoiceDialog createAddChoiceDialog(Context context,
                                                    LovelyChoiceDialog.OnItemSelectedListener<String> listener) {
        return new LovelyChoiceDialog(context)
                .setTopColorRes(R.color.colorAccent)
                .setTitle(context.getString(R.string.add_new_dialog_title))
                .setItems(context.getResources().getStringArray(R.array.add_new_dialog_items_list),
                        listener);
    }

    static LovelyCustomDialog createAddWordDialog(final Context context, final Bus bus,
                                                  List<String> groupNamesList, final List<String> wordsList) {
        LinearLayout customLayout = (LinearLayout) LayoutInflater
                .from(context)
                .inflate(R.layout.add_word_dialog_layout, null);
        final TextView errorMessage = customLayout.findViewById(R.id.error_message);
        final EditText wordInput = customLayout.findViewById(R.id.word_edit_text);
        wordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorMessage.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //Fulfill spinner with strings
        final Spinner groupsSpinner = customLayout.findViewById(R.id.group_choose_spinner);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, groupNamesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupsSpinner.setAdapter(spinnerAdapter);

        final LovelyCustomDialog dialog = new LovelyCustomDialog(context);
        dialog.setTopColorRes(R.color.colorAccent)
                .setTitle(context.getString(R.string.add_new_word_dialog_title))
                .setView(customLayout)
                .setListener(R.id.btn_negative, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setListener(R.id.btn_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String word = wordInput.getText().toString().trim();
                        boolean alreadyAdded = wordsList.contains(word.toLowerCase());
                        if (word.matches(WORD_INPUT_REGEX) && !alreadyAdded) {
                            String groupName = (String) groupsSpinner.getSelectedItem();
                            bus.post(new WordAddedEvent(new WordToTrack(word, groupName, true, wordsList.size())));
                            dialog.dismiss();
                        } else {
                            String msg = context.getString(alreadyAdded
                                    ? R.string.input_word_error_message_unique
                                    : R.string.input_word_error_message_regex);
                            errorMessage.setText(msg);
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
        return dialog;
    }

    static LovelyTextInputDialog createAddGroupDialog(Context context, final Bus bus,
                                                      final List<String> groupNamesList) {
        return new LovelyTextInputDialog(context)
                .setTopColorRes(R.color.colorAccent)
                .setTitle(context.getString(R.string.add_new_group_dialog_title))
                .setHint(context.getString(R.string.input_group_hint))
                .setInputFilter(context.getString(R.string.input_group_error_message),
                        new LovelyTextInputDialog.TextFilter() {
                            @Override
                            public boolean check(String text) {
                                text = text.trim();
                                return text.matches(GROUP_INPUT_REGEX)
                                        && !groupNamesList.contains(text.toLowerCase());
                            }
                        })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String groupName) {
                        bus.post(new GroupAddedEvent(new WordsGroupToTrack(groupName, 1)));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }

    private static LovelyTextInputDialog createNotificationsBaseDialog(Context context) {
        return new LovelyTextInputDialog(context)
                .setTopColorRes(R.color.colorAccent)
                .setTitleGravity(Gravity.CENTER_HORIZONTAL)
                .setHint(R.string.new_limit_hint)
                .setInputFilter(R.string.word_limit_creation_decimal_error,
                        new LovelyTextInputDialog.TextFilter() {
                            @Override
                            public boolean check(String text) {
                                try {
                                    int times = Integer.valueOf(text);
                                    if (times < 0 | times > 1000)
                                        return false;
                                } catch (NumberFormatException ex) {
                                    return false;
                                }
                                return true;
                            }
                        })
                .setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
    }

    static LovelyTextInputDialog createNotificationsEditDialog(Context context,
                                                               final Bus bus,
                                                               final Limiter currentLimiter,
                                                               final int groupHolderPos,
                                                               final int wordHolderPos) {
        LovelyTextInputDialog dialog = createNotificationsBaseDialog(context);
        dialog.setInitialInput(String.valueOf(currentLimiter.getLimit()));
        dialog
                .setTitle(R.string.change_notificaiton)
                .setMessage(R.string.limit_exists_text)
                .setNegativeButton(R.string.limit_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bus.post(new LimiterDeletedEvent(currentLimiter, wordHolderPos, groupHolderPos));
                    }
                })
                .setConfirmButton(R.string.limit_finish, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        int newLimit = Integer.valueOf(text);
                        if (newLimit != currentLimiter.getLimit()) {
                            currentLimiter.setLimit(newLimit);
                            bus.post(new LimiterChangedEvent(currentLimiter));
                        }
                    }
                })
                .setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
        return dialog;
    }

    static LovelyTextInputDialog createNotificationsAddDialog(Context context,
                                                              final Bus bus,
                                                              final String name,
                                                              final int wordHolderPos,
                                                              final int groupHolderPos) {
        final boolean forWord = wordHolderPos != -1;
        LovelyTextInputDialog dialog = createNotificationsBaseDialog(context);
        dialog
                .setTitle(R.string.new_limit_title)
                .setMessage(forWord ? R.string.word_limit_notification_creation_description
                        : R.string.group_limit_notification_creation_description)
                .setNegativeButton(R.string.limit_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setConfirmButton(R.string.limit_ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        int newLimit = Integer.valueOf(text);
                        Limiter limiter = new Limiter(name, newLimit, forWord);
                        bus.post(new LimiterAddedEvent(limiter, wordHolderPos, groupHolderPos));
                    }
                });
        return dialog;
    }

    public static LovelyCustomDialog createPersonalDataDialog(final Context context,
                                                              final PreferencesRepository prefsRepo) {
        LinearLayout customLayout = (LinearLayout) LayoutInflater
                .from(context)
                .inflate(R.layout.age_gender_layout, null);
        final TextView errorMessage = customLayout.findViewById(R.id.error_message);
        final EditText ageInput = customLayout.findViewById(R.id.age_edit_text);
        ageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorMessage.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final RadioGroup genderRadio = customLayout.findViewById(R.id.gender_radio_group);
        final LovelyCustomDialog dialog = new LovelyCustomDialog(context);

        dialog.setTopColorRes(R.color.colorAccent)
                .setTitle(R.string.personal_data_title)
                .setMessage(context.getString(R.string.personal_data_description))
                .setView(customLayout)
                .setCancelable(false)
                .setListener(R.id.btn_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String age = ageInput.getText().toString().trim();
                        int gender = genderRadio.getCheckedRadioButtonId();
                        if (age.matches(AGE_REGEX) && gender != -1) {
                            prefsRepo.setUserAge(Integer.parseInt(age));
                            prefsRepo.setUserGender(gender == 1);
                            prefsRepo.setPersonalDataGiven(true);
                            dialog.dismiss();
                        } else {
                            String msg = gender == -1
                                    ? context.getString(R.string.gender_error)
                                    : age.length() > 3
                                    ? context.getString(R.string.age_long_length)
                                    : context.getString(R.string.age_error);
                            errorMessage.setText(msg);
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
        return dialog;
    }

}
