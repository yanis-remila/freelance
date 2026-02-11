export enum NotificationType {
    RAPPEL_FIN_MISSION = 'RAPPEL_FIN_MISSION',
    MISSION_ASSIGNEE = 'MISSION_ASSIGNEE'
}

export interface Notification {
    id: number;
    freelancerId: number;
    missionId: number;
    type: NotificationType;
    message: string;
    createdAt: string;
    isRead: boolean;
}

export interface UnreadCountResponse {
    unreadCount: number;
}
