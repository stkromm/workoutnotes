package kromm.com.workoutnotes.workout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kromm.com.workoutnotes.R;
import kromm.com.workoutnotes.model.Exercise;
import kromm.com.workoutnotes.model.Workout;
import kromm.com.workoutnotes.util.OnSwipeTouchListener;

/**
 * Created by Steffen on 18.06.2016.
 */
public class WorkoutPageFragment extends Fragment implements Serializable
{
    LinearLayout exercises;
    Workout.WorkoutChangeListener listener;
    Workout workout;
    WorkoutActivity activity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_workout, container, false);
        exercises = (LinearLayout) rootView.findViewById(R.id.workout_exercises);
        setWorkout((Workout) getArguments().get("Workout"));
        activity = (WorkoutActivity) getActivity();
        ScrollView workoutScrollview = (ScrollView) rootView.findViewById(R.id.workout_scrollview);
        workoutScrollview.setOnTouchListener(new OnSwipeTouchListener(getActivity())
        {
            public void onSwipeTop()
            {
                activity.getSupportActionBar().setShowHideAnimationEnabled(true);
                activity.getSupportActionBar().hide();
            }

            public void onSwipeRight()
            {
            }

            public void onSwipeLeft()
            {
            }

            public void onSwipeBottom()
            {
                activity.getSupportActionBar().show();
            }

        });
        Button addExerciseButton = (Button) rootView.findViewById(R.id.workout_add_exercise);
        addExerciseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                workout.addExercise(new Exercise("Sit-Ups"));
            }
        });
        return rootView;
    }

    public void setWorkout(Workout workout)
    {
        if (this.workout != null) {
            this.workout.removeListener(listener);
        }
        this.workout = workout;
        listener = new Workout.WorkoutChangeListener()
        {
            @Override
            public void onChange(Workout workout, Exercise exercise, boolean removed)
            {
                if (exercise != null && removed) {
                    ExercisePanelFragment f = null;
                    for (ExercisePanelFragment fragment : exercisePanelFragmentList) {
                        if (fragment.exercise.equals(exercise)) {
                            f = fragment;
                            break;
                        }
                    }
                    if (f != null) {
                        exercisePanelFragmentList.remove(f);
                        f.dispose();
                    }
                }
                if (workout.numberOfExercises() <= 0) {
                    activity.setSelectedExercise(null);
                } else {
                    setUpView();
                }
            }
        };
        workout.addListener(listener);
        setUpView();
    }

    List<ExercisePanelFragment> exercisePanelFragmentList = new ArrayList<>();

    public void setUpView()
    {
        for (int i = 0; i < workout.numberOfExercises(); i++) {
            if (exercises.getChildCount() <= i) {
                createExercisePanel(workout.getExercise(i));
            } else {
                exercisePanelFragmentList.get(i).setExercise(workout.getExercise(i));
            }
        }
    }

    public void createExercisePanel(Exercise exercise)
    {
        ExercisePanelFragment exercisePanelFragment = new ExercisePanelFragment();
        exercisePanelFragmentList.add(exercisePanelFragment);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Exercise", exercise);
        exercisePanelFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(exercises.getId(), exercisePanelFragment,
                "exercise" + exercise).commit();

    }
}
