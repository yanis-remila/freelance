/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Client } from '../models/Client';
import type { Mission } from '../models/Mission';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ClientControllerService {
    /**
     * @returns Client OK
     * @throws ApiError
     */
    public static findClientByNom({
        nom,
    }: {
        nom: string,
    }): CancelablePromise<Client> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/clients',
            query: {
                'nom': nom,
            },
        });
    }
    /**
     * @returns Mission OK
     * @throws ApiError
     */
    public static findMissionByClientId({
        idClient,
    }: {
        idClient: number,
    }): CancelablePromise<Array<Mission>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/clients/{idClient}/mission',
            path: {
                'idClient': idClient,
            },
        });
    }
    /**
     * @returns Client OK
     * @throws ApiError
     */
    public static findClientByEmail({
        email,
    }: {
        email: string,
    }): CancelablePromise<Client> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/clients/email/{email}',
            query: {
                'email': email,
            },
        });
    }
}
