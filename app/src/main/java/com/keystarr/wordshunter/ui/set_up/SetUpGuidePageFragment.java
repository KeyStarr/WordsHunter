package com.keystarr.wordshunter.ui.set_up;


import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntroBaseFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.keystarr.wordshunter.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetUpGuidePageFragment extends AppIntroBaseFragment {

    protected static final String ARG_TITLE = "title";
    protected static final String ARG_TITLE_TYPEFACE = "title_typeface";
    protected static final String ARG_DESC = "desc";
    protected static final String ARG_DESC_TYPEFACE = "desc_typeface";
    protected static final String ARG_DRAWABLE = "drawable";
    protected static final String ARG_BG_COLOR = "bg_color";
    protected static final String ARG_TITLE_COLOR = "title_color";
    protected static final String ARG_DESC_COLOR = "desc_color";

    public static SetUpGuidePageFragment newInstance(CharSequence title,
                                                     CharSequence description,
                                                     @DrawableRes int imageDrawable) {
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(title);
        sliderPage.setDescription(description);
        sliderPage.setImageDrawable(imageDrawable);
        return newInstance(sliderPage);
    }

    public static SetUpGuidePageFragment newInstance(SliderPage sliderPage) {
        SetUpGuidePageFragment page = new SetUpGuidePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, sliderPage.getTitleString());
        args.putString(ARG_TITLE_TYPEFACE, sliderPage.getTitleTypeface());
        args.putString(ARG_DESC, sliderPage.getDescriptionString());
        args.putString(ARG_DESC_TYPEFACE, sliderPage.getDescTypeface());
        args.putInt(ARG_DRAWABLE, sliderPage.getImageDrawable());
        args.putInt(ARG_BG_COLOR, sliderPage.getBgColor());
        args.putInt(ARG_TITLE_COLOR, sliderPage.getTitleColor());
        args.putInt(ARG_DESC_COLOR, sliderPage.getDescColor());
        page.setArguments(args);

        return page;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_up_guide;
    }

}
