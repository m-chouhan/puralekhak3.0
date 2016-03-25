package com.mygdx.game;

import java.util.ArrayList;

/**An interface for local control-view messages
 * for passing messages to main activity
 * */
public interface UpdateViewCallback{

    void UnicodeEdited(String unicode);
    void ImageviewerReady(ControllerViewInterface cvInterface);
    void KeyboardSelected(int keyboard_id);
    void PatchSizeChanged(int row_size, int col_size);
    void UpdateProgress(int progress);
    void SpottingUpdated(ArrayList<item> itemArrayList, String unicode);
};
