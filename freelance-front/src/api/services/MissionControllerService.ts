/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { MissionFreelance } from '../models/MissionFreelance';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class MissionControllerService {
    /**
     * @returns MissionFreelance OK
     * @throws ApiError
     */
    public static linkMissionToFreelancer({
        missionId,
        freelancerId,
    }: {
        missionId: number,
        freelancerId: number,
    }): CancelablePromise<MissionFreelance> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/missions/{missionId}/link-freelancer/{freelancerId}',
            path: {
                'missionId': missionId,
                'freelancerId': freelancerId,
            },
        });
    }
}
