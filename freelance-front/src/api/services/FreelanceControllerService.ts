/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Freelancer } from '../models/Freelancer';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class FreelanceControllerService {
    /**
     * @returns Freelancer OK
     * @throws ApiError
     */
    public static getFreelanceById({
        id,
    }: {
        id: number,
    }): CancelablePromise<Freelancer> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/freelances/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns Freelancer OK
     * @throws ApiError
     */
    public static updateFreelance({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: Freelancer,
    }): CancelablePromise<Freelancer> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/freelances/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns any OK
     * @throws ApiError
     */
    public static deleteFreelance({
        id,
    }: {
        id: number,
    }): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/freelances/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns Freelancer OK
     * @throws ApiError
     */
    public static getAllFreelances(): CancelablePromise<Array<Freelancer>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/freelances',
        });
    }
    /**
     * @returns Freelancer OK
     * @throws ApiError
     */
    public static saveFreelance({
        requestBody,
    }: {
        requestBody: Freelancer,
    }): CancelablePromise<Freelancer> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/freelances',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns Freelancer OK
     * @throws ApiError
     */
    public static getFreelanceByEmail({
        email,
    }: {
        email: string,
    }): CancelablePromise<Freelancer> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/freelances/email/{email}',
            path: {
                'email': email,
            },
        });
    }
    /**
     * @returns any OK
     * @throws ApiError
     */
    public static deleteFreelanceByName({
        name,
    }: {
        name: string,
    }): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/freelances/test/{name}',
            path: {
                'name': name,
            },
        });
    }
}
