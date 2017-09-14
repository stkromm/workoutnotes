package kromm.com.workoutnotes.workout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import kromm.com.workoutnotes.R;
import kromm.com.workoutnotes.model.Exercise;
import kromm.com.workoutnotes.model.ExerciseSet;
import kromm.com.workoutnotes.util.Disposable;
import kromm.com.workoutnotes.util.OnSwipeTouchListener;

/**
 * Created by Steffen on 17.06.2016.
 */
public class ExercisePageFragment extends Fragment implements Disposable
{
    private Exercise exercise;
    private Exercise.ExerciseChangeListener listener;

    private LinearLayout sets;
    private TextView numberOfSetsText;
    private ViewGroup rootView;
    private TextView name;
    private TextView exerciseStatus;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_exercise, container, false);

        LinearLayout rootLayout = (LinearLayout) rootView.findViewById(R.id.root_layout);

        rootLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity())
        {
            public void onSwipeTop()
            {
            }

            public void onSwipeRight()
            {
            }

            public void onSwipeLeft()
            {
                WorkoutActivity workActivity = ((WorkoutActivity) getActivity());
                workActivity.selectNextExercise();
            }

            public void onSwipeBottom()
            {
            }

        });
        final Button addSetButton = (Button) rootView.findViewById(R.id.exercise_add_set);
        addSetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ExerciseSet set = new ExerciseSet(0, 0, exercise);
                exercise.addSet(set);
                addSet(set);
            }
        });

        numberOfSetsText = (TextView) rootView.findViewById(R.id.exercise_number_sets);
        listener = new Exercise.ExerciseChangeListener()
        {
            @Override
            public void onChange(Exercise exercise)
            {
                numberOfSetsText.setText(String.valueOf(exercise.getSets().size()));
            }
        };
        FloatingActionButton finishButton = (FloatingActionButton) rootView.findViewById(R.id
                .exercise_finish_current);
        finishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exercise.setState(Exercise.ExerciseState.DONE);
                WorkoutActivity workActivity = ((WorkoutActivity) getActivity());
                workActivity.selectNextExercise();
            }
        });
        FloatingActionButton skipButton = (FloatingActionButton) rootView.findViewById(R.id
                .exercise_skip_current);
        skipButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exercise.setState(Exercise.ExerciseState.SKIPPED);
                WorkoutActivity workActivity = ((WorkoutActivity) getActivity());
                workActivity.selectNextExercise();
            }
        });
        setExercise((Exercise) getArguments().get("Exercise"));
        sets = (LinearLayout) rootView.findViewById(R.id
                .exercise_sets);

        name = (TextView) rootView.findViewById(R.id.textView5);
        exerciseStatus = (TextView) rootView.findViewById(R.id.exercise_status_text);
        return rootView;
    }

    public void setExercise(Exercise exercise)
    {
        if (this.exercise != null) {
            exercise.removeChangeListener(listener);
        }
        this.exercise = exercise;
        exercise.addChangeListener(listener);

        if (exerciseStatus != null)
            switch (exercise.getState()) {
                case DONE:
                    exerciseStatus.setText("Done");
                    exerciseStatus.setBackground(ResourcesCompat.getDrawable(getResources(), R
                            .drawable.exercise_done, null));
                    break;
                case SKIPPED:
                    exerciseStatus.setText("Previously Skipped");
                    exerciseStatus.setBackground(ResourcesCompat.getDrawable(getResources(), R
                                    .drawable.exercise_skipped,
                            null));
                    break;
                case NORMAL:
                    exerciseStatus.setText("Performing");
                    exerciseStatus.setBackground(ResourcesCompat.getDrawable(getResources(), R
                                    .drawable.panel,
                            null));
                    break;
                default:
                    break;
            }

        if (sets == null) {
            return;
        }
        numberOfSetsText.setText(exercise.getSets().size() + "");
        name.setText(exercise.getName());
        sets.removeAllViews();
        for (ExerciseSet set : this.exercise.getSets()) {
            addSet(set);
        }
    }

    public Exercise getExercise()
    {
        return exercise;
    }

    public void addSet(ExerciseSet set)
    {
        ExerciseSetFragment exerciseSetFragment = new ExerciseSetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Set", set);
        exerciseSetFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(sets.getId(), exerciseSetFragment,
                "exercise" + exercise).commit();
    }

    @Override
    public void dispose()
    {
        exercise.removeChangeListener(listener);
        exercise = null;
        rootView.removeAllViews();
        getFragmentManager().beginTransaction().remove(this).commit();
        ((WorkoutActivity) getActivity()).getAdapter().notifyDataSetChanged();
    }
}
