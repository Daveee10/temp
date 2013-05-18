package com.android.systemui.statusbar.quicksettings.quicktile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.android.systemui.R;
import com.android.systemui.statusbar.quicksettings.QuickSettingsContainerView;
import com.android.systemui.statusbar.quicksettings.QuickSettingsController;

public class TorchTile extends QuickSettingsTile {

    public TorchTile(Context context, LayoutInflater inflater,
            QuickSettingsContainerView container,
            QuickSettingsController qsc, Handler handler) {
        super(context, inflater, container, qsc);

        updateTileState();

        mOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bright = Settings.System.getInt(mContext.getContentResolver(),
                     Settings.System.EXPANDED_FLASH_MODE, 0) == 1;
                Intent i = new Intent("net.cactii.flash2.TOGGLE_FLASHLIGHT");
                i.putExtra("bright", bright);
                mContext.sendBroadcast(i);
            }
        };

        mOnLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("net.cactii.flash2", "net.cactii.flash2.MainActivity");
                startSettingsActivity(intent);
                return true;
            }
        };

        qsc.registerObservedContent(Settings.System.getUriFor(Settings.System.TORCH_STATE), this);
    }

    private void updateTileState() {
        boolean enabled = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.TORCH_STATE, 0) == 1;
        String label = mContext.getString(R.string.quick_settings_torch);

        // Show OFF next to the label when in OFF state, ON/IN USE is indicated by the color
        if(enabled) {
            mDrawable = R.drawable.stat_flashlight_on;
            mLabel = label;
        } else {
            mDrawable = R.drawable.stat_flashlight_off;
            mLabel = label + " " + mContext.getString(R.string.quick_settings_label_disabled);
        }
    }

    @Override
    public void onChangeUri(ContentResolver resolver, Uri uri) {
        updateTileState();
        updateQuickSettings();
    }
}
