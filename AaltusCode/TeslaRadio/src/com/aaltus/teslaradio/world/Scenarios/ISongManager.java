package com.aaltus.teslaradio.world.Scenarios;

import com.aaltus.teslaradio.subject.AudioOptionEnum;
import com.aaltus.teslaradio.subject.SongEnum;

/**
 * Created by Jean-Christophe on 2014-11-18.
 */
public interface ISongManager {

    public void onSetNewSong(SongEnum songEnum);

    public void onAudioOptionTouched(final AudioOptionEnum optionEnum);


}
