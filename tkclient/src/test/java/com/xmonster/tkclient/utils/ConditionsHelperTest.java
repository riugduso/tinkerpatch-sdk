package com.xmonster.tkclient.utils;

import android.text.TextUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class ConditionsHelperTest {
    private Map<String, String> props;
    @Test
    public void toReversePolish1() throws Exception {
        Assert.assertTrue(
            Conditions.Helper.toReversePolish("test>1 && (test<2 || uid==100)").equals(
                Arrays.asList("test>1", "test<2", "uid==100", "|", "&"))
        );
    }

    @Test
    public void toReversePolish2() throws Exception {
        Assert.assertTrue(
            Conditions.Helper.toReversePolish("test==1").equals(Collections.singletonList("test==1"))
        );
    }

    @Test
    public void calcReversePolish() throws Exception {
        List<String> list = Arrays.asList("test>1", "city==shanghai", "&");
        Assert.assertTrue(Conditions.Helper.calcReversePolish(list, props));

        List<String> list1 = Arrays.asList("test>2", "city==shanghai", "&");
        Assert.assertFalse(Conditions.Helper.calcReversePolish(list1, props));

        List<String> list2 = Arrays.asList("test==2", "city!=shangha", "&");
        Assert.assertTrue(Conditions.Helper.calcReversePolish(list2, props));
    }

    @Test
    public void calc1() throws Exception {
        List<String> list = Conditions.Helper.toReversePolish(
            "(test!=1&amp;&amp;city==shanghai)||(uid&gt;=101)||(deviceid&lt;10000)"
        );
        Assert.assertTrue(Conditions.Helper.calcReversePolish(list, props));
    }

    @Test
    public void calc2() throws Exception {
        Map<String, String> props1 = new HashMap<>();
        props1.put("test", "1");
        List<String> list = Conditions.Helper.toReversePolish(
            "(test!=1&amp;&amp;city==shanghai)||(uid&gt;=101)||(deviceid&lt;10000)"
        );
        Assert.assertFalse(Conditions.Helper.calcReversePolish(list, props1));
    }

    @Test
    public void calc() throws Exception {
        List<String> list = Conditions.Helper.toReversePolish("test>1 && (test<2 || uid==100)");
        Assert.assertFalse(Conditions.Helper.calcReversePolish(list, props));

        List<String> list1 = Conditions.Helper.toReversePolish("test>1 && (test<2 || uid==101)");
        Assert.assertTrue(Conditions.Helper.calcReversePolish(list1, props));
    }

    @Test
    public void calcExpr() throws Exception {
        Assert.assertTrue(Conditions.Helper.calcExpr("test>=1", props));
        Assert.assertFalse(Conditions.Helper.calcExpr("test>2", props));
        Assert.assertTrue(Conditions.Helper.calcExpr("test<=3", props));
    }

    @Test
    public void calcExpr1() throws Exception {
        Assert.assertFalse(Conditions.Helper.calcExpr("city>shanghai", props));
        Assert.assertTrue(Conditions.Helper.calcExpr("city>=shanghai", props));
        Assert.assertTrue(Conditions.Helper.calcExpr("city==shanghai", props));
        Assert.assertTrue(Conditions.Helper.calcExpr("city!=guangdong", props));
    }

    @Test
    public void splitExpr() throws Exception {
        Assert.assertTrue(
            Conditions.Helper.splitExpr("test>=1").equals(Arrays.asList("test", ">=", "1"))
        );
    }

    @Before
    public void setup() {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.join(any(CharSequence.class), any(Iterable.class)))
            .thenAnswer(new Answer<String>() {
                @Override
                public String answer(InvocationOnMock invocation) throws Throwable {
                    StringBuilder sb = new StringBuilder();
                    CharSequence delimiter = (CharSequence) invocation.getArguments()[0];
                    Iterator<?> it = ((Iterable) invocation.getArguments()[1]).iterator();
                    if (it.hasNext()) {
                        sb.append(it.next());
                        while (it.hasNext()) {
                            sb.append(delimiter);
                            sb.append(it.next());
                        }
                    }
                    return sb.toString();
                }
            });

        props = new HashMap<>();
        props.put("test", "2");
        props.put("city", "shanghai");
        props.put("uid", "101");
    }
}