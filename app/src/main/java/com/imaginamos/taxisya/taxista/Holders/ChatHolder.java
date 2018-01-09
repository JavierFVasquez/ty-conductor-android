package com.imaginamos.taxisya.taxista.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imaginamos.taxisya.taxista.R;

/**
 * Created by javiervasquez on 23/12/17.
 */

public class ChatHolder extends RecyclerView.ViewHolder {

    private RelativeLayout RL_Message_Container_User;
    private TextView TV_Message_Text_User;
    private RelativeLayout RL_Message_Container_Taxi;
    private TextView TV_Message_Text_Taxi;

    public ChatHolder(View itemView) {
        super(itemView);

        RL_Message_Container_User = (RelativeLayout) itemView.findViewById(R.id.RL_Message_Container_User);
        TV_Message_Text_User = (TextView) itemView.findViewById(R.id.TV_Message_Text_User);
        RL_Message_Container_Taxi = (RelativeLayout) itemView.findViewById(R.id.RL_Message_Container_Taxi);
        TV_Message_Text_Taxi = (TextView) itemView.findViewById(R.id.TV_Message_Text_Taxi);

    }

    public RelativeLayout getRL_Message_Container_User() {
        return RL_Message_Container_User;
    }

    public TextView getTV_Message_Text_User() {
        return TV_Message_Text_User;
    }

    public RelativeLayout getRL_Message_Container_Taxi() {
        return RL_Message_Container_Taxi;
    }

    public TextView getTV_Message_Text_Taxi() {
        return TV_Message_Text_Taxi;
    }
}
