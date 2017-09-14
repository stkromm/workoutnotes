package kromm.com.workoutnotes.workout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import kromm.com.workoutnotes.R;
import kromm.com.workoutnotes.model.ExerciseSet;
import kromm.com.workoutnotes.util.Disposable;

/**
 * Created by Steffen on 22.06.2016.
 */
public class ExerciseSetFragment extends Fragment implements Disposable
{
    ExerciseSet set;
    ViewGroup rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_set, container, false);
        final EditText weightCount = (EditText) rootView.findViewById(R.id.exercise_set_weight);
        weightCount.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                try {
                    set.setWeight(Float.valueOf(weightCount.getText().toString()));
                } catch (NumberFormatException e) {
                }
                return false;
            }
        });
        final EditText repetitionCount = (EditText) rootView.findViewById(R.id.exercise_set_reps);
        repetitionCount.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                try {
                    set.setRepetitions(Integer.valueOf(repetitionCount.getText().toString()));
                } catch (NumberFormatException e) {
                }
                return false;
            }
        });
        final ImageButton removeButton = (ImageButton) rootView.findViewById(R.id
                .remove_set_button);
        removeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dispose();
            }
        });
        set = (ExerciseSet) getArguments().get("Set");
        repetitionCount.setText(String.valueOf(set.getRepetitions()));
        weightCount.setText(String.valueOf(set.getWeight()));
        return rootView;
    }

    @Override
    public void dispose()
    {
        rootView.removeAllViews();
        set.dispose();
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
