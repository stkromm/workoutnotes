package kromm.com.workoutnotes.workout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import kromm.com.workoutnotes.R;
import kromm.com.workoutnotes.model.Exercise;
import kromm.com.workoutnotes.util.Disposable;

/**
 * Created by Steffen on 22.06.2016.
 */
public class ExercisePanelFragment extends Fragment implements Disposable
{
    Exercise exercise;
    ViewGroup rootView;
    TextView name;
    TextView subtitle;
    Exercise.ExerciseChangeListener listener;
    LinearLayout background;
    WorkoutActivity activity;

    ViewPager.OnPageChangeListener drawListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            if (exercise.equals(activity.getSelectedExercise())) {
                rootView.requestFocus();
            }
        }

        @Override
        public void onPageSelected(int position)
        {
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Init
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_panel, container, false);
        activity = (WorkoutActivity) getActivity();
        background = (LinearLayout) rootView.findViewById(R.id.exercise_background);
        background.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus && !exercise.equals(activity.getSelectedExercise())) {
                    activity.setSelectedExercise(exercise);
                    activity.getPager().setCurrentItem(1);
                }
            }
        });
        background.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                activity.getPager().setCurrentItem(1);
            }
        });
        listener = new Exercise.ExerciseChangeListener()
        {
            @Override
            public void onChange(Exercise exercise)
            {
                setUpView();
            }
        };
        Bundle args = getArguments();
        setExercise((Exercise) args.get("Exercise"));
        final ImageButton removeButton = (ImageButton) rootView.findViewById(R.id
                .panel_right_button);

        final Context context = getContext();
        removeButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (activity.getWorkout().numberOfExercises() == 1) {
                        Toast error = Toast.makeText(context, "You need atleast 1 exercise", Toast
                                .LENGTH_SHORT);
                        error.show();

                    } else {
                        dispose();
                    }
                    return true;
                }
                return false;
            }
        });

        name = (TextView) rootView.findViewById(R.id.panel_title);
        subtitle = (TextView) rootView.findViewById(R.id.panel_subtitle);
        activity.getPager().addOnPageChangeListener(drawListener);
        setUpView();
        return rootView;
    }

    public void setUpView()
    {
        if (name != null)
            name.setText(exercise.getName());
        if (subtitle != null)
            subtitle.setText(exercise.getSets().size() + " Sets");
        if (background != null)
            switch (exercise.getState()) {
                case DONE:
                    background.setBackground(ResourcesCompat.getDrawable(getResources(), R
                            .drawable.exercise_done, null));
                    break;
                case SKIPPED:
                    background.setBackground(ResourcesCompat.getDrawable(getResources(), R
                            .drawable.exercise_skipped,
                            null));
                    break;
                case NORMAL:
                    background.setBackground(ResourcesCompat.getDrawable(getResources(), R
                            .drawable.panel,
                            null));
                    break;
                default:
                    break;
            }
    }

    public void setExercise(Exercise exercise)
    {
        if (this.exercise != null) {
            this.exercise.removeChangeListener(listener);
        }
        this.exercise = exercise;
        exercise.addChangeListener(listener);
        setUpView();
    }

    @Override
    public void dispose()
    {
        exercise.removeChangeListener(listener);
        rootView.removeAllViews();
        activity.getPager().removeOnPageChangeListener(drawListener);
        activity.getWorkout().removeExercise(exercise);
    }
}
