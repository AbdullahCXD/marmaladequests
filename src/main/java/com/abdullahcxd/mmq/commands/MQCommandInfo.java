package com.abdullahcxd.mmq.commands;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MQCommandInfo {

    private String name, description, usage, permission;
    private int minArgs;
    private boolean asPlayer;

}
