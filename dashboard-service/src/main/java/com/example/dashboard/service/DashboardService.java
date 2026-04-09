package com.example.dashboard.service;

import com.example.dashboard.dto.response.ActivityResponse;
import com.example.dashboard.dto.response.StatResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    public List<StatResponse> getStats() {
        return List.of(
                new StatResponse("Total Users", "12,430", "group",          "bg-blue-500"),
                new StatResponse("Revenue",     "$48,200", "attach_money",  "bg-green-500"),
                new StatResponse("Orders",      "3,820",   "shopping_cart", "bg-purple-500"),
                new StatResponse("Growth",      "+18%",    "trending_up",   "bg-orange-500")
        );
    }

    public List<ActivityResponse> getActivity() {
        return List.of(
                new ActivityResponse("person_add",   "New user registered",        "2 mins ago",  "text-blue-500"),
                new ActivityResponse("check_circle", "Order #1042 completed",      "15 mins ago", "text-green-500"),
                new ActivityResponse("warning",      "Server CPU above 80%",       "1 hour ago",  "text-orange-500"),
                new ActivityResponse("star",         "New 5-star review received", "3 hours ago", "text-yellow-500")
        );
    }
}
