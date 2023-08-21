package com.team2470.trigger;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class XboxControllerTrigger extends Trigger {

    private final XboxController m_controller;
    private final XboxController.Axis m_axis;
    private final double m_threshold;

    public XboxControllerTrigger(XboxController controller, XboxController.Axis axis) {
        this(controller, axis, 0.1);
    }

    public XboxControllerTrigger(XboxController controller, XboxController.Axis axis, double threshold) {
        m_controller = controller;
        m_axis = axis;
        m_threshold = threshold;

        if (m_axis != XboxController.Axis.kLeftTrigger && m_axis != XboxController.Axis.kRightTrigger) {
            throw new IllegalArgumentException("unexpected axis: "+m_axis);
        }

        if (m_threshold <= 0) {
            throw new IllegalArgumentException("invalid threshold: "+m_threshold);
        }
    }

    @Override
    public boolean get() {
        switch (m_axis) {
            case kLeftTrigger:
                return m_controller.getLeftTriggerAxis() > m_threshold;
            case kRightTrigger:
                return m_controller.getRightTriggerAxis() > m_threshold;
            default:
                return false;
        }
    }

}